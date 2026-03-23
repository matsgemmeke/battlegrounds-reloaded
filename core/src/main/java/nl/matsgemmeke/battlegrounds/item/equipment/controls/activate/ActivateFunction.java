package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class ActivateFunction implements ItemFunction<EquipmentUser> {

    private final Equipment equipment;

    public ActivateFunction(Equipment equipment) {
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

    public boolean perform(EquipmentUser user) {
        if (!user.canDeploy()) {
            return false;
        }

        equipment.activateDeployment(user);
        return true;
    }
}
