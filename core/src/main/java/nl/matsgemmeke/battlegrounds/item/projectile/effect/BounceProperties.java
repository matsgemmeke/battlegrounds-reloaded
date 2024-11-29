package nl.matsgemmeke.battlegrounds.item.projectile.effect;

public record BounceProperties(
        int amountOfBounces,
        double bounceFactor,
        double velocityRetention,
        long checkDelay,
        long checkPeriod
) {}
