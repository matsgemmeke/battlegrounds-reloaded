package nl.matsgemmeke.battlegrounds.item.equipment.controls.cook;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class CookFunction implements ItemFunction<EquipmentUser> {

    private final Equipment equipment;
    private final PrimeDeployment deployment;

    public CookFunction(Equipment equipment, PrimeDeployment deployment) {
        this.equipment = equipment;
        this.deployment = deployment;
    }

    public boolean isAvailable() {
        return !equipment.isAwaitingDeployment();
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

        equipment.performDeployment(deployment, user);
        return true;
    }
}
