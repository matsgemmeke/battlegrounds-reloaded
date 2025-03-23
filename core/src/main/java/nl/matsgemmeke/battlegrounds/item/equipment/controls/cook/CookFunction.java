package nl.matsgemmeke.battlegrounds.item.equipment.controls.cook;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public class CookFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private final Equipment equipment;
    @NotNull
    private final PrimeDeployment deployment;

    public CookFunction(@NotNull Equipment equipment, @NotNull PrimeDeployment deployment) {
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

    public boolean perform(@NotNull EquipmentHolder holder) {
        equipment.performDeployment(deployment, holder);
        return true;
    }
}
