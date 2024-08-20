package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.PlantDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    private PlantDeployment deployment;

    public PlantFunction(
            @NotNull Deployable item,
            @NotNull ItemMechanismActivation mechanismActivation,
            @NotNull PlantDeployment deployment,
            @NotNull AudioEmitter audioEmitter
    ) {
        this.item = item;
        this.mechanismActivation = mechanismActivation;
        this.deployment = deployment;
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

        audioEmitter.playSounds(sounds, adjacentBlock.getLocation());

        item.onDeploy();
        mechanismActivation.prime(holder);
        deployment.plant(adjacentBlock, targetBlockFace);
        return true;
    }
}
