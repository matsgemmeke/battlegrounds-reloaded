package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.drop.DropDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;

public class DropFunction implements ItemFunction<EquipmentHolder> {

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
    public boolean perform(EquipmentHolder holder) {
        if (!holder.canDeploy()) {
            return false;
        }

        equipment.performDeployment(deployment, holder);
        return true;
    }
}
