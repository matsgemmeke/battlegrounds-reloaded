package nl.matsgemmeke.battlegrounds.item.deploy.place;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PlaceDeployment implements Deployment {

    private static final int TARGET_BLOCK_SCAN_DISTANCE = 4;

    private final AudioEmitter audioEmitter;
    @Nullable
    private PlaceDeploymentProperties properties;

    @Inject
    public PlaceDeployment(AudioEmitter audioEmitter) {
        this.audioEmitter = audioEmitter;
    }

    public void configureProperties(PlaceDeploymentProperties properties) {
        this.properties = properties;
    }

    @NotNull
    public DeploymentResult perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        return DeploymentResult.failure();
    }

    public Optional<DeploymentContext> createContext(Deployer deployer, Entity deployerEntity) {
        if (properties == null) {
            throw new IllegalStateException("Cannot perform deployment without properties configured");
        }

        List<Block> targetBlocks = deployer.getLastTwoTargetBlocks(TARGET_BLOCK_SCAN_DISTANCE);

        if (targetBlocks.size() != 2 || !targetBlocks.get(1).getType().isOccluding()) {
            return Optional.empty();
        }

        Block targetBlock = targetBlocks.get(1);
        Block adjacentBlock = targetBlocks.get(0);
        BlockFace targetBlockFace = targetBlock.getFace(adjacentBlock);

        if (targetBlockFace == null) {
            return Optional.empty();
        }

        this.placeBlock(adjacentBlock, targetBlockFace, properties.material());

        PlaceDeploymentObject object = new PlaceDeploymentObject(adjacentBlock, properties.material());
        object.setCooldown(properties.cooldown());
        object.setHealth(properties.health());
        object.setResistances(properties.resistances());

        audioEmitter.playSounds(properties.placeSounds(), adjacentBlock.getLocation());

        deployer.setHeldItem(null);

        return Optional.of(new DeploymentContext(deployerEntity, object, deployer, object));
    }

    private void placeBlock(Block block, BlockFace blockFace, Material material) {
        block.setType(material);

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
