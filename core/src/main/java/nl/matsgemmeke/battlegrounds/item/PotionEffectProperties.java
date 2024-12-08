package nl.matsgemmeke.battlegrounds.item;

public record PotionEffectProperties(
        int duration,
        int amplifier,
        boolean ambient,
        boolean particles,
        boolean icon
) {}
