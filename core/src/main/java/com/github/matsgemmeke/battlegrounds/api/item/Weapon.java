package com.github.matsgemmeke.battlegrounds.api.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Weapon extends Item {

    /**
     * Gets the current operating mode being executed on the weapon. Returns null if the weapon has no ongoing
     * operation.
     *
     * @return the current operating mode
     */
    @Nullable
    OperatingMode getOperatingMode();

    /**
     * Sets the current operating mode being executed on the weapon.
     *
     * @param operatingMode the current operating mode
     */
    void setOperatingMode(@Nullable OperatingMode operatingMode);

    /**
     * Updates the {@link ItemStack} of the weapon.
     *
     * @return whether the weapon was updated
     */
    boolean update();
}
