package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface SmokeScreenEffectFactory {

    ItemEffect create(SmokeScreenProperties properties);
}
