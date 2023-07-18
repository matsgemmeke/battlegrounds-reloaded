package com.github.matsgemmeke.battlegrounds.api.entity;

import com.github.matsgemmeke.battlegrounds.api.item.PlayerEffect;
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

    /**
     * Adds a {@link PlayerEffect} to the player.
     *
     * @param effect the effect
     * @return whether the effect was added
     */
    boolean addEffect(@NotNull PlayerEffect effect);

    /**
     * Removes a {@link PlayerEffect} from the player.
     *
     * @param effect the effect
     * @return whether the effect was removed
     */
    boolean removeEffect(@NotNull PlayerEffect effect);
}
