package nl.matsgemmeke.battlegrounds.item.effect.flash;

public record FlashProperties(
        double range,
        float explosionPower,
        boolean explosionSetFire,
        boolean explosionBreakBlocks
) {}
