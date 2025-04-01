package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceDeployment implements Deployment {

    private static final int TARGET_BLOCK_SCAN_DISTANCE = 4;

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final PlaceDeploymentProperties deploymentProperties;

    public PlaceDeployment(@NotNull PlaceDeploymentProperties deploymentProperties, @NotNull AudioEmitter audioEmitter) {
        this.deploymentProperties = deploymentProperties;
        this.audioEmitter = audioEmitter;
    }

    @NotNull
    public DeploymentResult perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        List<Block> targetBlocks = deployer.getLastTwoTargetBlocks(TARGET_BLOCK_SCAN_DISTANCE);

        if (targetBlocks.size() != 2 || !targetBlocks.get(1).getType().isOccluding()) {
            return DeploymentResult.failure();
        }

        Block targetBlock = targetBlocks.get(1);
        Block adjacentBlock = targetBlocks.get(0);
        BlockFace targetBlockFace = targetBlock.getFace(adjacentBlock);

        if (targetBlockFace == null) {
            return DeploymentResult.failure();
        }

        this.placeBlock(adjacentBlock, targetBlockFace);

        PlaceDeploymentObject object = new PlaceDeploymentObject(adjacentBlock, deploymentProperties.material());
        object.setCooldown(deploymentProperties.cooldown());
        object.setHealth(deploymentProperties.health());
        object.setResistances(deploymentProperties.resistances());

        audioEmitter.playSounds(deploymentProperties.placeSounds(), adjacentBlock.getLocation());

        deployer.setHeldItem(null);

        return DeploymentResult.success(object);
    }

    private void placeBlock(@NotNull Block block, @NotNull BlockFace blockFace) {
        block.setType(deploymentProperties.material());

        FaceAttachable.AttachedFace attachedFace = this.getCorrespondingAttachedFace(blockFace);
        BlockState placedBlockState = block.getState();

        FaceAttachable faceAttachable = (FaceAttachable) block.getBlockData();
        faceAttachable.setAttachedFace(attachedFace);

        placedBlockState.setBlockData(faceAttachable);

        if (attachedFace == FaceAttachable.AttachedFace.WALL) {
            Directional directional = (Directional) block.getBlockData();
            directional.setFacing(blockFace);

            placedBlockState.setBlockData(directional);
        }

        placedBlockState.update(true, true);
    }

    @NotNull
    private FaceAttachable.AttachedFace getCorrespondingAttachedFace(@NotNull BlockFace blockFace) {
        switch (blockFace) {
            case UP -> {
                return FaceAttachable.AttachedFace.FLOOR;
            }
            case DOWN -> {
                return FaceAttachable.AttachedFace.CEILING;
            }
            default -> {
                return FaceAttachable.AttachedFace.WALL;
            }
        }
    }
}
