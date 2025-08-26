package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;

import java.util.List;

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
     * Gets all registered items that are assigned to a given holder.
     *
     * @param holder the item holder
     * @return a list of items assigned to the holder
     */
    List<Gun> getAssignedGuns(GunHolder holder);

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
     * @param holder the holder associated with the item
     */
    void register(Gun gun, GunHolder holder);
}
