package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import org.jetbrains.annotations.Nullable;

public interface Equipment extends Weapon, Interactable<EquipmentHolder> {

    /**
     * Gets the holder of the equipmemt item. Returns null if the equipment does not have a holder.
     *
     * @return the equipment holder or null if it does not have one
     */
    @Nullable
    EquipmentHolder getHolder();

    /**
     * Sets the holder of the equipment item.
     *
     * @param holder the equipment holder
     */
    void setHolder(@Nullable EquipmentHolder holder);
}
