package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class FireModeSpec {

    @Required
    @EnumValue(FireModeType.class)
    public String type;
    public Integer amountOfShots;
    public Integer rateOfFire;
    public Long cycleCooldown;

    private enum FireModeType {
        BURST, FULLY_AUTOMATIC, SEMI_AUTOMATIC
    }
}
