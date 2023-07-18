package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the attachment that is present on top of a gun that allows a player to zoom in while shooting.
 */
public interface ScopeAttachment {

    /**
     * Applies the scope effect to a {@link BattleItemHolder}.
     *
     * @param holder the entity
     * @return whether the effect was applied
     */
    boolean applyEffect(@NotNull BattleItemHolder holder);

    /**
     * Gets whether the scope attachment is being used by a player.
     *
     * @return whether the scope is being used
     */
    boolean isScoped();

    /**
     * Attempts to change the scope magnification to the next setting. Returns false if unable to change to another
     * setting.
     *
     * @return true if changed to another magnification settings, otherwise false
     */
    boolean nextMagnification();

    /**
     * Removes the scope effect from the current player.
     *
     * @return whether the effect was removed
     */
    boolean removeEffect();
}
