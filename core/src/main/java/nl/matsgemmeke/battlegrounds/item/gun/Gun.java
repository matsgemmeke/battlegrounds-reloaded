package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
import nl.matsgemmeke.battlegrounds.item.scope.Scopable;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.jetbrains.annotations.Nullable;

public interface Gun extends Weapon, Interactable<GunHolder>, Reloadable, Scopable, Shootable {

    /**
     * Gets the holder of the gun. Returns null if the gun does not have a holder.
     *
     * @return the gun holder or null if it does not have one
     */
    @Nullable
    GunHolder getHolder();

    /**
     * Sets the holder of the gun.
     *
     * @param holder the gun holder
     */
    void setHolder(@Nullable GunHolder holder);

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

    int getRateOfFire();
}
