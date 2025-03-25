package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public class ActivateFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private final Equipment equipment;

    public ActivateFunction(@NotNull Equipment equipment) {
        this.equipment = equipment;
    }

    public boolean isAvailable() {
        return equipment.isActivatorReady();
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
        equipment.activateDeployment(holder, holder.getEntity());
        return true;
    }
}
