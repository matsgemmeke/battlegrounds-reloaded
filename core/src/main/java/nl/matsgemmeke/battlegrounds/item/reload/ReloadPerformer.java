package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.item.audio.SoundSource;

/**
 * An entity that is capable of performing reloads.
 */
public interface ReloadPerformer extends SoundSource {

    /**
     * Applies effects to the entity for when they are performing a reload.
     */
    void applyReloadingState();

    /**
     * Removes effects from the entity for when they stop performing a reload.
     */
    void resetReloadingState();
}
