package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Gun extends Weapon, AmmunitionHolder, Interactable<GunHolder>, Shootable {

    @NotNull
    FireMode getFireMode();

    void setFireMode(@NotNull FireMode fireMode);

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

    @NotNull
    List<GameSound> getShotSounds();

    void setShotSounds(@NotNull List<GameSound> shotSounds);

    /**
     * Makes the gun shoot one of its projectiles.
     *
     * @return whether the gun has shot
     */
    boolean shoot();
}
