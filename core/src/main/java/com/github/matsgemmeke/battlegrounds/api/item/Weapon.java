package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.WeaponHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Weapon extends Item {

    /**
     * Gets the holder of the weapon. Returns null if the weapon does not have a holder.
     *
     * @return the weapon holder or null if it does not have one
     */
    @Nullable
    WeaponHolder getHolder();

    /**
     * Sets the holder of the weapon.
     *
     * @param holder the weapon holder
     */
    void setHolder(@Nullable WeaponHolder holder);

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
