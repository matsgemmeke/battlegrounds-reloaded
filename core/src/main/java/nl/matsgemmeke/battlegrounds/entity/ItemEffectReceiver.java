package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.item.ItemEffect;

/**
 * An entity able to receive effects from items.
 */
public interface ItemEffectReceiver {

    /**
     * Adds an {@link ItemEffect} to the player.
     *
     * @param effect the effect
     * @return whether the effect was added
     */
    boolean addEffect(ItemEffect effect);

    /**
     * Removes an {@link ItemEffect} from the player.
     *
     * @param effect the effect
     * @return whether the effect was removed
     */
    boolean removeEffect(ItemEffect effect);
}
