package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class PlaceDeploymentTest {

    private static final double HEALTH = 20.0;
    private static final List<GameSound> PLACE_SOUNDS = Collections.emptyList();
    private static final long COOLDOWN = 10L;
    private static final Map<DamageType, Double> RESISTANCES = Collections.emptyMap();
    private static final Material MATERIAL = Material.WARPED_BUTTON;

    private AudioEmitter audioEmitter;
    private Deployer deployer;
    private Entity deployerEntity;
    private PlaceDeploymentProperties properties;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        deployer = mock(Deployer.class);
        deployerEntity = mock(Entity.class);
        properties = new PlaceDeploymentProperties(PLACE_SOUNDS, RESISTANCES, MATERIAL, HEALTH, COOLDOWN);
    }

    @Test
    public void performThrowsIllegalStateExceptionWhenNoPropertiesAreConfigured() {
        PlaceDeployment deployment = new PlaceDeployment(audioEmitter);

        assertThatThrownBy(() -> deployment.perform(deployer, deployerEntity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot perform deployment without properties configured");
    }

    @Test
    public void performReturnsFailureResultIfDeployerDoesNotReturnTwoTargetBlocks() {
        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        PlaceDeployment deployment = new PlaceDeployment(audioEmitter);
        deployment.configureProperties(properties);
        DeploymentResult result = deployment.perform(deployer, deployerEntity);

        assertThat(result.success()).isFalse();

        verifyNoInteractions(audioEmitter);
    }

    @Test
    public void performReturnsFailureResultIfDeployerIsTargetingOccludingBlock() {
        Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.OAK_FENCE);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(targetBlock, targetBlock));

        PlaceDeployment deployment = new PlaceDeployment(audioEmitter);
        deployment.configureProperties(properties);
        DeploymentResult result = deployment.perform(deployer, deployerEntity);

        assertThat(result.success()).isFalse();

        verifyNoInteractions(audioEmitter);
    }

    @Test
    public void performReturnsFailureResultIfAdjacentBlockIsNotConnectedToTargetBlock() {
        Block adjacentBlock = mock(Block.class);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(null);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceDeployment deployment = new PlaceDeployment(audioEmitter);
        deployment.configureProperties(properties);
        DeploymentResult result = deployment.perform(deployer, deployerEntity);

        assertThat(result.success()).isFalse();

        verifyNoInteractions(audioEmitter);
    }

    @Test
    public void performReturnsSuccessResultWhenPlacingBlockAgainstCeiling() {
        BlockFace targetBlockFace = BlockFace.DOWN;
        BlockState adjacentBlockState = mock(BlockState.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location adjacentBlockLocation = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable);
        when(adjacentBlock.getLocation()).thenReturn(adjacentBlockLocation);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceDeployment deployment = new PlaceDeployment(audioEmitter);
        deployment.configureProperties(properties);
        DeploymentResult result = deployment.perform(deployer, deployerEntity);

        assertThat(result.success()).isTrue();
        assertThat(result.object().getCooldown()).isEqualTo(COOLDOWN);
        assertThat(result.object().getHealth()).isEqualTo(HEALTH);
        assertThat(result.object().getLocation()).isEqualTo(adjacentBlockLocation);

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, adjacentBlockLocation);
        verify(deployer).setHeldItem(null);
        verify(faceAttachable).setAttachedFace(AttachedFace.CEILING);
    }

    @Test
    public void performReturnsSuccessResultWhenDeployingBlockAgainstFloor() {
        BlockFace targetBlockFace = BlockFace.UP;
        BlockState adjacentBlockState = mock(BlockState.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location adjacentBlockLocation = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable);
        when(adjacentBlock.getLocation()).thenReturn(adjacentBlockLocation);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceDeployment deployment = new PlaceDeployment(audioEmitter);
        deployment.configureProperties(properties);
        DeploymentResult result = deployment.perform(deployer, deployerEntity);

        assertThat(result.success()).isTrue();
        assertThat(result.object().getCooldown()).isEqualTo(COOLDOWN);
        assertThat(result.object().getHealth()).isEqualTo(HEALTH);
        assertThat(result.object().getLocation()).isEqualTo(adjacentBlockLocation);

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, adjacentBlockLocation);
        verify(deployer).setHeldItem(null);
        verify(faceAttachable).setAttachedFace(AttachedFace.FLOOR);
    }

    @Test
    public void performReturnsTrueWhenPlacingBlockAgainstWall() {
        BlockFace targetBlockFace = BlockFace.NORTH;
        BlockState adjacentBlockState = mock(BlockState.class);
        Directional directional = mock(Directional.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location adjacentBlockLocation = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable).thenReturn(directional);
        when(adjacentBlock.getLocation()).thenReturn(adjacentBlockLocation);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        when(deployer.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceDeployment deployment = new PlaceDeployment(audioEmitter);
        deployment.configureProperties(properties);
        DeploymentResult result = deployment.perform(deployer, deployerEntity);

        assertThat(result.success()).isTrue();
        assertThat(result.object().getCooldown()).isEqualTo(COOLDOWN);
        assertThat(result.object().getHealth()).isEqualTo(HEALTH);
        assertThat(result.object().getLocation()).isEqualTo(adjacentBlockLocation);

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(adjacentBlockState).setBlockData(directional);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, adjacentBlockLocation);
        verify(deployer).setHeldItem(null);
        verify(directional).setFacing(targetBlockFace);
        verify(faceAttachable).setAttachedFace(AttachedFace.WALL);
    }
}
