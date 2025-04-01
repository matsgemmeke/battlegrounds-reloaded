package nl.matsgemmeke.battlegrounds.item.scope;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the attachment that is present on top of a gun that allows its user to zoom in while shooting.
 */
public interface ScopeAttachment {

    /**
     * Applies the scope effect to a {@link ScopeUser}.
     *
     * @param scopeUser the entity
     * @return whether the effect was applied
     */
    boolean applyEffect(@NotNull ScopeUser scopeUser);

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
