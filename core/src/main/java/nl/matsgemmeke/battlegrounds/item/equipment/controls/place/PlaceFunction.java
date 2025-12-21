package nl.matsgemmeke.battlegrounds.item.equipment.controls.place;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;

public class PlaceFunction implements ItemFunction<EquipmentHolder> {

    private final Equipment equipment;
    private final PlaceDeployment deployment;

    public PlaceFunction(Equipment equipment, PlaceDeployment deployment) {
        this.equipment = equipment;
        this.deployment = deployment;
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
    public boolean perform(EquipmentHolder holder) {
        if (!holder.canDeploy()) {
            return false;
        }

        equipment.performDeployment(deployment, holder);
        return true;
    }
}
