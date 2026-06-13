package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.actor.BlockActor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentContext;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.object.BlockDeploymentObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.block.data.type.Switch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlaceDeploymentActionTest {

    private static final String ITEM_NAME = "Test Item";
    private static final double HEALTH = 20.0;
    private static final List<GameSound> PLACE_SOUNDS = Collections.emptyList();
    private static final long COOLDOWN = 10L;
    private static final Map<DamageType, Double> RESISTANCES = Collections.emptyMap();
    private static final Material MATERIAL = Material.WARPED_BUTTON;

    private static final PlaceDeploymentProperties PROPERTIES = new PlaceDeploymentProperties(PLACE_SOUNDS, RESISTANCES, MATERIAL, HEALTH, COOLDOWN);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private Deployer deployer;
    @Mock
    private DestructionListener destructionListener;
    @Mock
    private HitboxResolver hitboxResolver;
    @InjectMocks
    private PlaceDeploymentAction deploymentAction;

    @Test
    @DisplayName("perform throws IllegalStateException when no properties are configured")
    void perform_noPropertiesConfigured() {
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);

        assertThatThrownBy(() -> deploymentAction.perform(context))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot perform deployment without properties configured");
    }

    @Test
    @DisplayName("perform returns empty optional when deployer does not return two target blocks")
    void perform_deployerWithoutTargetBlocks() {
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        deploymentAction.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).isEmpty();

        verifyNoInteractions(audioEmitter);
    }

    @Test
    @DisplayName("perform returns empty optional when deployer is targeting an occluding block")
    void perform_deployerTargetsOccludingBlock() {
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.OAK_FENCE);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(targetBlock, targetBlock));

        deploymentAction.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).isEmpty();

        verifyNoInteractions(audioEmitter);
    }

    @Test
    @DisplayName("perform returns empty optional when adjacent block is not connected to target block")
    void perform_unconnectedAdjacentBlock() {
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);
        Block adjacentBlock = mock(Block.class);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(null);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        deploymentAction.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).isEmpty();

        verifyNoInteractions(audioEmitter);
    }

    static List<Arguments> placeBlockArguments() {
        return List.of(
                arguments(BlockFace.DOWN, AttachedFace.CEILING),
                arguments(BlockFace.UP, AttachedFace.FLOOR)
        );
    }

    @ParameterizedTest
    @MethodSource("placeBlockArguments")
    @DisplayName("perform returns optional with deployment result when placing block vertically")
    void perform_verticalPlacement(BlockFace targetBlockFace, AttachedFace expectedAttachedFace) {
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);
        BlockState adjacentBlockState = mock(BlockState.class);
        Switch switchBlockData = mock(Switch.class);
        Location adjacentBlockLocation = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(switchBlockData);
        when(adjacentBlock.getLocation()).thenReturn(adjacentBlockLocation);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        deploymentAction.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOfSatisfying(BlockDeploymentObject.class, deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isEqualTo(HEALTH);
                assertThat(deploymentObject.getLocation()).isEqualTo(adjacentBlockLocation);
            });
            assertThat(deploymentResult.cooldown()).isEqualTo(COOLDOWN);
        });

        verify(adjacentBlockState).setBlockData(switchBlockData);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, adjacentBlockLocation);
        verify(deployer).setHeldItem(null);
        verify(switchBlockData).setAttachedFace(expectedAttachedFace);
    }

    @Test
    @DisplayName("perform returns optional with deployment result when placing block horizontally")
    void perform_horizontalPlacement() {
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);
        BlockFace targetBlockFace = BlockFace.NORTH;
        BlockState adjacentBlockState = mock(BlockState.class);
        Switch switchBlockData = mock(Switch.class);
        Location adjacentBlockLocation = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(switchBlockData);
        when(adjacentBlock.getLocation()).thenReturn(adjacentBlockLocation);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        deploymentAction.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOfSatisfying(BlockDeploymentObject.class, deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isEqualTo(HEALTH);
                assertThat(deploymentObject.getLocation()).isEqualTo(adjacentBlockLocation);
            });
            assertThat(deploymentResult.actor()).isInstanceOf(BlockActor.class);
            assertThat(deploymentResult.cooldown()).isEqualTo(COOLDOWN);
        });

        verify(adjacentBlockState, times(2)).setBlockData(switchBlockData);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, adjacentBlockLocation);
        verify(deployer).setHeldItem(null);
        verify(switchBlockData).setFacing(targetBlockFace);
        verify(switchBlockData).setAttachedFace(AttachedFace.WALL);
    }
}
