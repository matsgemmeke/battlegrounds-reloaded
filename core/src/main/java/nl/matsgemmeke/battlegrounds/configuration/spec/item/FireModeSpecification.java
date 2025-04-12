package nl.matsgemmeke.battlegrounds.configuration.spec.item;

public record FireModeSpecification(
        String type,
        Integer amountOfShots,
        Integer rateOfFire,
        Long delayBetweenShots
) { }
