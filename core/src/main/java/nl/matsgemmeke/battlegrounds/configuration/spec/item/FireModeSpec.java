package nl.matsgemmeke.battlegrounds.configuration.spec.item;

public record FireModeSpec(
        String type,
        Integer amountOfShots,
        Integer rateOfFire,
        Long delayBetweenShots
) { }
