package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.BlockTriggerTarget;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Entity;
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
class PlaceDeploymentTest {

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
    private Entity deployerEntity;
    @Mock
    private HitboxResolver hitboxResolver;
    @InjectMocks
    private PlaceDeployment deployment;

    @Test
    void performThrowsIllegalStateExceptionWhenNoPropertiesAreConfigured() {
        assertThatThrownBy(() -> deployment.perform(deployer, deployerEntity, destructionListener))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot perform deployment without properties configured");
    }

    @Test
    void performReturnsEmptyOptionalWhenDeployerDoesNotReturnTwoTargetBlocks() {
        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        deployment.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, destructionListener);

        assertThat(deploymentResultOptional).isEmpty();

        verifyNoInteractions(audioEmitter);
    }

    @Test
    void performReturnsEmptyOptionalWhenDeployerIsTargetingOccludingBlock() {
        Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.OAK_FENCE);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(targetBlock, targetBlock));

        deployment.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, destructionListener);

        assertThat(deploymentResultOptional).isEmpty();

        verifyNoInteractions(audioEmitter);
    }

    @Test
    void performReturnsEmptyOptionalWhenAdjacentBlockIsNotConnectedToTargetBlock() {
        Block adjacentBlock = mock(Block.class);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(null);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        deployment.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, destructionListener);

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
    void performReturnsOptionalWithDeploymentContextWhenPlacingBlockVertically(BlockFace targetBlockFace, AttachedFace expectedAttachedFace) {
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

        deployment.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, destructionListener);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).satisfies(deploymentObject -> {
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
    void performReturnsOptionalWithDeploymentContextWhenPlacingBlockHorizontally() {
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

        deployment.configureProperties(PROPERTIES);
        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, destructionListener);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).satisfies(deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isEqualTo(HEALTH);
                assertThat(deploymentObject.getLocation()).isEqualTo(adjacentBlockLocation);
            });
            assertThat(deploymentResult.triggerTarget()).isInstanceOf(BlockTriggerTarget.class);
            assertThat(deploymentResult.cooldown()).isEqualTo(COOLDOWN);
        });

        verify(adjacentBlockState, times(2)).setBlockData(switchBlockData);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, adjacentBlockLocation);
        verify(deployer).setHeldItem(null);
        verify(switchBlockData).setFacing(targetBlockFace);
        verify(switchBlockData).setAttachedFace(AttachedFace.WALL);
    }
}
