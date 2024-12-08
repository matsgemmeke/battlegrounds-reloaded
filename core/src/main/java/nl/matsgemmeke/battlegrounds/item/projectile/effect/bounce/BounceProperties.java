package nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce;

public record BounceProperties(
        int amountOfBounces,
        double horizontalFriction,
        double verticalFriction,
        long checkDelay,
        long checkPeriod
) {}
