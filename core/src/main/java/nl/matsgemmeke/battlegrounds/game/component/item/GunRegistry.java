package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface GunRegistry {

    /**
     * Assigns the given gun to the given holder. This method will do nothing when the given gun is not registered to
     * the registry, or when it is already assigned.
     *
     * @param gun the gun
     * @param holder the holder to assign the gun to
     */
    void assign(Gun gun, GunHolder holder);

    /**
     * Removes the assigned holder from the given gun and registers it as unassigned. This method will do nothing when
     * the given gun is not registered to the registry or has no assigned holder.
     *
     * @param gun the gun to be unassigned from its holder
     */
    void unassign(Gun gun);

    /**
     * Gets the gun that is assigned to a given holder and corresponds with a given item stack. The returned optional
     * will be empty when no matching gun was found.
     *
     * @param holder the holder to which the gun should be assigned to
     * @param itemStack the corresponding item stack
     * @return an optional containing the matching gun or empty when not found
     */
    Optional<Gun> getAssignedGun(GunHolder holder, ItemStack itemStack);

    /**
     * Gets all registered items that are assigned to a given holder.
     *
     * @param holder the item holder
     * @return a list of items assigned to the holder
     */
    List<Gun> getAssignedGuns(GunHolder holder);

    /**
     * Gets the unassigned gun that corresponds with a given item stack. The returned optional will be empty when no
     * matching gun was found.
     *
     * @param itemStack the corresponding item stack
     * @return an optional containing the matching gun or empty when not found
     */
    Optional<Gun> getUnassignedGun(ItemStack itemStack);

    /**
     * Registers an unassigned gun to the registry.
     *
     * @param gun the gun to be registered
     */
    void register(Gun gun);

    /**
     * Registers a gun to the registry with an assigned holder.
     *
     * @param gun the gun to be registered
     * @param holder the holder associated with the gun
     */
    void register(Gun gun, GunHolder holder);
}
