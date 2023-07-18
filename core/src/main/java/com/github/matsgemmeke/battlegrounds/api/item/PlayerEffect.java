package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;

/**
 * Represents an effect on a {@link BattleItem} that is able to influence a {@link BattlePlayer}.
 */
public interface PlayerEffect {

    /**
     * Applies the effect.
     */
    void apply();

    /**
     * Removes the effect.
     */
    void remove();

    /**
     * Attempts to update the effect on the player.
     *
     * @return whether the effect was updated
     */
    boolean update();
}
