package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.PlantDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlantFunction implements ItemFunction<EquipmentHolder> {

    private static final int TARGET_BLOCK_SCAN_DISTANCE = 4;

    @NotNull
    private Deployable item;
    @NotNull
    private ItemMechanismActivation mechanismActivation;
    @NotNull
    private PlantDeployment deployment;

    public PlantFunction(@NotNull Deployable item, @NotNull ItemMechanismActivation mechanismActivation, @NotNull PlantDeployment deployment) {
        this.item = item;
        this.mechanismActivation = mechanismActivation;
        this.deployment = deployment;
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

        item.onDeploy();
        mechanismActivation.prime(holder);
        deployment.plant(adjacentBlock, targetBlockFace);
        return true;
    }
}
