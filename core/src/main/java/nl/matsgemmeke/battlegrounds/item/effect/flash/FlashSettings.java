package nl.matsgemmeke.battlegrounds.item.effect.flash;

public record FlashSettings(
        double range,
        float explosionPower,
        boolean explosionSetFire,
        boolean explosionBreakBlocks
) { }
