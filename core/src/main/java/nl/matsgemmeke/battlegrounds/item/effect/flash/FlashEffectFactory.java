package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface FlashEffectFactory {

    ItemEffect create(FlashProperties properties);
}
