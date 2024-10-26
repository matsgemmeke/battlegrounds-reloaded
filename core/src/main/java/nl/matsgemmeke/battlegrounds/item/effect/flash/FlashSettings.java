package nl.matsgemmeke.battlegrounds.item.effect.flash;

public record FlashSettings(
        double range,
        int effectDuration,
        int effectAmplifier,
        boolean effectAmbient,
        boolean effectParticles,
        boolean effectIcon,
        float explosionPower,
        boolean explosionSetFire,
        boolean explosionBreakBlocks
) { }
