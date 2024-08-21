package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.RotatableBlock;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
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

public class PlantFunction implements ItemFunction<EquipmentHolder> {

    private static final int TARGET_BLOCK_SCAN_DISTANCE = 4;

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private Deployable item;
    @NotNull
    private ItemMechanismActivation mechanismActivation;
    @NotNull
    private Iterable<GameSound> sounds;
    @NotNull
    private Material material;

    public PlantFunction(
            @NotNull Deployable item,
            @NotNull ItemMechanismActivation mechanismActivation,
            @NotNull Material material,
            @NotNull AudioEmitter audioEmitter
    ) {
        this.item = item;
        this.mechanismActivation = mechanismActivation;
        this.material = material;
        this.audioEmitter = audioEmitter;
        this.sounds = new HashSet<>();
    }

    public void addSounds(@NotNull Iterable<GameSound> sounds) {
        this.sounds = Iterables.concat(this.sounds, sounds);
    }

    public boolean isAvailable() {
        return !item.isDeployed();
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
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

        this.plantBlock(adjacentBlock, targetBlockFace);

        RotatableBlock rotatableBlock = new RotatableBlock(adjacentBlock);
        item.onDeploy(rotatableBlock);

        audioEmitter.playSounds(sounds, adjacentBlock.getLocation());

        mechanismActivation.prime(holder);
        return true;
    }

    private void plantBlock(@NotNull Block block, @NotNull BlockFace blockFace) {
        block.setType(material);

        AttachedFace attachedFace = this.getCorrespondingAttachedFace(blockFace);
        BlockState plantBlockState = block.getState();

        FaceAttachable faceAttachable = (FaceAttachable) block.getBlockData();
        faceAttachable.setAttachedFace(attachedFace);

        plantBlockState.setBlockData(faceAttachable);

        if (attachedFace == FaceAttachable.AttachedFace.WALL) {
            Directional directional = (Directional) block.getBlockData();
            directional.setFacing(blockFace);

            plantBlockState.setBlockData(directional);
        }

        plantBlockState.update(true, true);
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
