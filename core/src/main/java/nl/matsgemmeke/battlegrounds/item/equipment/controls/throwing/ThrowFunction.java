package nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public class ThrowFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private final Equipment equipment;
    @NotNull
    private final ThrowDeployment deployment;

    public ThrowFunction(@NotNull Equipment equipment, @NotNull ThrowDeployment deployment) {
        this.equipment = equipment;
        this.deployment = deployment;
    }

    public boolean isAvailable() {
        return !equipment.isDeployed();
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return equipment.isPerformingDeployment();
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        equipment.performDeployment(deployment, holder);
        return true;
    }
}
