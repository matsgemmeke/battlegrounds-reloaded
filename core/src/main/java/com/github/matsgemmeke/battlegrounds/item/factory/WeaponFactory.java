package com.github.matsgemmeke.battlegrounds.item.factory;

import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import org.jetbrains.annotations.NotNull;

public interface WeaponFactory<T extends Weapon> {

    /**
     * Make a new weapon based on the given id. This id should equal the one from the weapon configuration files.
     *
     * @param context the context the weapon is situated in
     * @param id the id of the weapon
     * @return a new instance of the corresponding item
     */
    @NotNull
    T make(@NotNull GameContext context, @NotNull String id);
}
