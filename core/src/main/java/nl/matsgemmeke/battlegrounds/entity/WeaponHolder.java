package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.item.Weapon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity that is capable of holding and operating a {@link Weapon}.
 */
public interface WeaponHolder extends ItemHolder, RecoilReceiver {

    /**
     * Adds a {@link Weapon} to the holder.
     *
     * @param weapon the weapon
     * @return whether the weapon was added
     */
    boolean addWeapon(@NotNull Weapon weapon);

    /**
     * Gets the direction where the entity would aim a weapon.
     *
     * @return the entity's aiming direction
     */
    @NotNull
    Location getAimDirection();

    /**
     * Attempts to find a {@link Weapon} based on its {@link ItemStack}. Returns null if there were no matches.
     *
     * @param itemStack the item stack
     * @return the corresponding weapon or null if there were no matches
     */
    @Nullable
    Weapon getWeapon(@NotNull ItemStack itemStack);

    /**
     * Removes a weapon from the holder.
     *
     * @param weapon the weapon to remove
     * @return whether the weapon was removed
     */
    boolean removeWeapon(@NotNull Weapon weapon);
}
