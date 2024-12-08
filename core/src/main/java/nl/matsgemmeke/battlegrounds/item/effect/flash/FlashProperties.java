package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import org.jetbrains.annotations.NotNull;

public record FlashProperties(
        @NotNull PotionEffectProperties potionEffect,
        double range,
        float explosionPower,
        boolean explosionSetFire,
        boolean explosionBreakBlocks
) {}
