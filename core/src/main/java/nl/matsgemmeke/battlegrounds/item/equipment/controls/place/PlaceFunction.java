package nl.matsgemmeke.battlegrounds.item.equipment.controls.place;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public class PlaceFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private final Equipment equipment;
    @NotNull
    private final PlaceDeployment deployment;

    public PlaceFunction(@NotNull Equipment equipment, @NotNull PlaceDeployment deployment) {
        this.equipment = equipment;
        this.deployment = deployment;
    }

    public boolean isAvailable() {
        return !equipment.isDeployed();
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        equipment.performDeployment(deployment, holder);
        return true;
    }
}
