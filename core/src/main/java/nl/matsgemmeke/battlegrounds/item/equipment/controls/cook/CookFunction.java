package nl.matsgemmeke.battlegrounds.item.equipment.controls.cook;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.deploy.action.PrimeDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class CookFunction implements Function<EquipmentUser> {

    private final Equipment equipment;
    private final PrimeDeploymentAction deploymentAction;

    public CookFunction(Equipment equipment, PrimeDeploymentAction deploymentAction) {
        this.equipment = equipment;
        this.deploymentAction = deploymentAction;
    }

    @Override
    public boolean isBlocking() {
        return false;
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
        if (!equipment.isAwaitingDeployment() || !user.canDeploy()) {
            return FunctionResult.DENIED;
        }

        equipment.performDeploymentAction(deploymentAction, user);

        return FunctionResult.SUCCESS;
    }
}
