package nl.matsgemmeke.battlegrounds.item.projectile.effect;

public record BounceProperties(
        int amountOfBounces,
        double frictionFactor,
        long checkDelay,
        long checkPeriod
) {}
