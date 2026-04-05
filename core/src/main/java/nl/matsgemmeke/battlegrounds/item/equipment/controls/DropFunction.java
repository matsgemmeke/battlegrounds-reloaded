package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.drop.DropDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class DropFunction implements ItemFunction<EquipmentUser> {

    private final Equipment equipment;
    private final DropDeploymentAction deploymentAction;

    public DropFunction(Equipment equipment, DropDeploymentAction deploymentAction) {
        this.equipment = equipment;
        this.deploymentAction = deploymentAction;
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

        equipment.performDeploymentAction(deploymentAction, user);
        return true;
    }
}
