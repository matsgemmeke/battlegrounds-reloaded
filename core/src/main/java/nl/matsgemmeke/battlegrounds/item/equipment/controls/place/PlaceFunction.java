package nl.matsgemmeke.battlegrounds.item.equipment.controls.place;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.PlacedBlock;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceFunction implements ItemFunction<EquipmentHolder> {

    private static final int TARGET_BLOCK_SCAN_DISTANCE = 4;

    @NotNull
    private final AudioEmitter audioEmitter;
    private boolean performing;
    @NotNull
    private final Equipment equipment;
    @NotNull
    private final PlaceProperties properties;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public PlaceFunction(
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull PlaceProperties properties,
            @Assisted @NotNull Equipment equipment,
            @Assisted @NotNull AudioEmitter audioEmitter
    ) {
        this.properties = properties;
        this.equipment = equipment;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.performing = false;
    }

    public boolean isAvailable() {
        return true;
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return performing;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        ItemEffect effect = equipment.getEffect();

        if (effect == null) {
            throw new ItemFunctionException("Cannot perform place function for equipment item \"" + equipment.getName() + "\"; it has no effect!");
        }

        List<Block> targetBlocks = holder.getLastTwoTargetBlocks(TARGET_BLOCK_SCAN_DISTANCE);

        if (targetBlocks.size() != 2 || !targetBlocks.get(1).getType().isOccluding()) {
            return false;
        }

        Block targetBlock = targetBlocks.get(1);
        Block adjacentBlock = targetBlocks.get(0);
        BlockFace targetBlockFace = targetBlock.getFace(adjacentBlock);

        if (targetBlockFace == null) {
            return false;
        }

        this.placeBlock(adjacentBlock, targetBlockFace);

        PlacedBlock placedBlock = new PlacedBlock(adjacentBlock, properties.material());
        DeploymentProperties deploymentProperties = equipment.getDeploymentProperties();

        if (deploymentProperties != null) {
            placedBlock.setHealth(deploymentProperties.getHealth());
            placedBlock.setResistances(deploymentProperties.getResistances());
        }

        audioEmitter.playSounds(properties.placeSounds(), adjacentBlock.getLocation());

        performing = true;

        taskRunner.runTaskLater(() -> performing = false, properties.delayAfterPlacement());

        holder.setHeldItem(null);

        if (!effect.isPrimed()) {
            effect.prime(new ItemEffectContext(holder, placedBlock));
        } else {
            effect.deploy(placedBlock);
        }

        equipment.onDeployDeploymentObject(placedBlock);
        return true;
    }

    private void placeBlock(@NotNull Block block, @NotNull BlockFace blockFace) {
        block.setType(properties.material());

        AttachedFace attachedFace = this.getCorrespondingAttachedFace(blockFace);
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
    private AttachedFace getCorrespondingAttachedFace(@NotNull BlockFace blockFace) {
        switch (blockFace) {
            case UP -> {
                return AttachedFace.FLOOR;
            }
            case DOWN -> {
                return AttachedFace.CEILING;
            }
            default -> {
                return AttachedFace.WALL;
            }
        }
    }
}
