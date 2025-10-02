package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;

public record FlashProperties(PotionEffectProperties potionEffect, double range, float power, boolean breakBlocks, boolean setFire) {
}
