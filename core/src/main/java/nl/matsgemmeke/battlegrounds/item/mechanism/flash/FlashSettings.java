package nl.matsgemmeke.battlegrounds.item.mechanism.flash;

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
