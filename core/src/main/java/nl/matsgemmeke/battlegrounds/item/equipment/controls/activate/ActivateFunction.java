package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;

public class ActivateFunction implements Function<EquipmentUser> {

    private final Equipment equipment;

    public ActivateFunction(Equipment equipment) {
        this.equipment = equipment;
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
        if (!equipment.isActivatorReady() || !user.canDeploy()) {
            return FunctionResult.FAILED;
        }

        equipment.activateDeployment(user);

        return FunctionResult.SUCCESS;
    }
}
