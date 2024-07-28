package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.entity.ItemEffectReceiver;

public interface ScopeUser extends ItemEffectReceiver {

    /**
     * Alters the entity's view magnification by a given value.
     *
     * @param magnification the magnification value
     */
    void applyViewMagnification(float magnification);
}
