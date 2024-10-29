package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployableSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.PlacedBlock;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

public class PlaceFunction implements ItemFunction<EquipmentHolder> {

    private static final int TARGET_BLOCK_SCAN_DISTANCE = 4;

    @NotNull
    private AudioEmitter audioEmitter;
    private boolean performing;
    @NotNull
    private DeployableSource item;
    @NotNull
    private ItemEffectActivation effectActivation;
    @NotNull
    private Iterable<GameSound> sounds;
    private long delayAfterPlacement;
    @NotNull
    private Material material;
    @NotNull
    private TaskRunner taskRunner;

    public PlaceFunction(
            @NotNull DeployableSource item,
            @NotNull ItemEffectActivation effectActivation,
            @NotNull Material material,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            long delayAfterPlacement
    ) {
        this.item = item;
        this.effectActivation = effectActivation;
        this.material = material;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.delayAfterPlacement = delayAfterPlacement;
        this.performing = false;
        this.sounds = new HashSet<>();
    }

    public void addSounds(@NotNull Iterable<GameSound> sounds) {
        this.sounds = Iterables.concat(this.sounds, sounds);
    }

    public boolean isAvailable() {
        return item.getDeployedObjects().isEmpty();
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

        audioEmitter.playSounds(sounds, adjacentBlock.getLocation());

        performing = true;

        taskRunner.runTaskLater(() -> performing = false, delayAfterPlacement);

        effectActivation.prime(holder, new PlacedBlock(adjacentBlock, material));
        return true;
    }

    private void placeBlock(@NotNull Block block, @NotNull BlockFace blockFace) {
        block.setType(material);

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
