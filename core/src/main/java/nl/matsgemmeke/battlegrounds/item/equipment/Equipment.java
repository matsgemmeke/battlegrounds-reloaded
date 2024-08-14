package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Equipment extends Weapon, Droppable, Interactable<EquipmentHolder> {

    /**
     * Gets the activator item stack used to manually activate the equipment. Returns null if the equipment has no
     * activator.
     *
     * @return the equipment's activator item stack or null if not present
     */
    @Nullable
    ItemStack getActivatorItemStack();

    /**
     * Sets the activator item stack used to manually activate the equipment.
     *
     * @param activatorItemStack the equipment's activator item stack
     */
    void setActivatorItemStack(@Nullable ItemStack activatorItemStack);

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
