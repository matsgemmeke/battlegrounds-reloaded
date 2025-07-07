package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class FireModeSpec {

    @Required
    @EnumValue(type = FireModeType.class)
    public String type;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "BURST_MODE")
    public Integer amountOfShots;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "BURST_MODE", "FULLY_AUTOMATIC" })
    public Integer rateOfFire;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "SEMI_AUTOMATIC")
    public Long cycleCooldown;

    private enum FireModeType {
        BURST, FULLY_AUTOMATIC, SEMI_AUTOMATIC
    }
}
