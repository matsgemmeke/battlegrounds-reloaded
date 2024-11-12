package nl.matsgemmeke.battlegrounds.item.effect;

public record PotionEffectSettings(
        int duration,
        int amplifier,
        boolean ambient,
        boolean particles,
        boolean icon
) { }
