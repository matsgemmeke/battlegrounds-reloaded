package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
import nl.matsgemmeke.battlegrounds.item.scope.Scopable;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.jetbrains.annotations.Nullable;

public interface Gun extends Weapon, Interactable<GunUser>, Reloadable, Scopable, Shootable {

    /**
     * Gets the scope attachment of the gun. Returns null if the gun has no scope attachment in place.
     *
     * @return the scope attachment
     */
    @Nullable
    ScopeAttachment getScopeAttachment();

    /**
     * Sets the scope attachment of the gun.
     *
     * @param scopeAttachment the scope attachment
     */
    void setScopeAttachment(@Nullable ScopeAttachment scopeAttachment);

    /**
     * Gets the user of the gun. Returns null if the gun does not have a user.
     *
     * @return the gun user or null if it does not have one
     */
    @Nullable
    GunUser getUser();

    /**
     * Sets the user of the gun.
     *
     * @param user the gun user
     */
    void setUser(@Nullable GunUser user);

    int getRateOfFire();
}
