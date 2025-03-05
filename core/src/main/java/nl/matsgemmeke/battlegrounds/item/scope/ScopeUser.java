package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.entity.ItemEffectReceiver;
import nl.matsgemmeke.battlegrounds.item.audio.SoundSource;

public interface ScopeUser extends ItemEffectReceiver, SoundSource {

    /**
     * Alters the entity's view magnification by a given value.
     *
     * @param magnification the magnification value
     */
    void applyViewMagnification(float magnification);
}
