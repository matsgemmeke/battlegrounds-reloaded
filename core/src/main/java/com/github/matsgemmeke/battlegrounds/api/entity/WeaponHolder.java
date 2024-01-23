package com.github.matsgemmeke.battlegrounds.api.entity;

import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an entity that is capable of holding and operating a {@link Weapon}.
 */
public interface WeaponHolder extends ItemHolder, RecoilReceiver {

    /**
     * Gets the direction where the entity would aim a weapon.
     *
     * @return the entity's aiming direction
     */
    @NotNull
    Location getAimDirection();
}
