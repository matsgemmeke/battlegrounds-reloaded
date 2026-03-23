package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.drop.DropDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class DropFunction implements ItemFunction<EquipmentUser> {

    private final Equipment equipment;
    private final DropDeployment deployment;

    public DropFunction(Equipment equipment, DropDeployment deployment) {
        this.equipment = equipment;
        this.deployment = deployment;
    }

    @Override
    public boolean isAvailable() {
        return equipment.isDeployed();
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public boolean perform(EquipmentUser user) {
        if (!user.canDeploy()) {
            return false;
        }

        equipment.performDeployment(deployment, user);
        return true;
    }
}
