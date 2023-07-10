package com.github.matsgemmeke.battlegrounds.api.entity;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A player object which holds information and can perform actions in Battlegrounds.
 */
public interface BattlePlayer extends BattleEntity, BattleItemHolder {

    /**
     * Gets the {@link Player} entity of the object.
     *
     * @return the player entity
     */
    @NotNull
    Player getEntity();
}
