package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;

/**
 * Represents an effect on a {@link Item} that is able to influence a {@link GamePlayer}.
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
