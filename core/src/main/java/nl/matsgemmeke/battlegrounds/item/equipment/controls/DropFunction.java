package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.deploy.action.DropDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class DropFunction implements Function<EquipmentUser> {

    private final Equipment equipment;
    private final DropDeploymentAction deploymentAction;

    public DropFunction(Equipment equipment, DropDeploymentAction deploymentAction) {
        this.equipment = equipment;
        this.deploymentAction = deploymentAction;
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
    public FunctionResult perform(EquipmentUser user) {
        if (!equipment.isDeployed() || !user.canDeploy()) {
            return FunctionResult.DENIED;
        }

        equipment.performDeploymentAction(deploymentAction, user);

        return FunctionResult.CANCELLED;
    }
}
