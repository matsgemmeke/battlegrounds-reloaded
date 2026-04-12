package nl.matsgemmeke.battlegrounds.item.equipment.controls.place;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.deploy.action.PlaceDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class PlaceFunction implements Function<EquipmentUser> {

    private final Equipment equipment;
    private final PlaceDeploymentAction deploymentAction;

    public PlaceFunction(Equipment equipment, PlaceDeploymentAction deploymentAction) {
        this.equipment = equipment;
        this.deploymentAction = deploymentAction;
    }

    @Override
    public boolean isAvailable() {
        return !equipment.isDeployed();
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
