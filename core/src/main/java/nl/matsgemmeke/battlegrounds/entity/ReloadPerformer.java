package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.game.audio.AudioSource;

/**
 * An entity that is capable of performing reloads.
 */
public interface ReloadPerformer extends AudioSource {

    /**
     * Applies effects to the entity for when they are performing a reload.
     */
    void applyReloadingState();

    /**
     * Removes effects from the entity for when they stop performing a reload.
     */
    void resetReloadingState();
}
