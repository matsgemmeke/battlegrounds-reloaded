package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class RecoilSpec {

    @Required
    @EnumValue(type = RecoilType.class)
    public String type;
    @Required
    public Float[] horizontal;
    @Required
    public Float[] vertical;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "CAMERA_MOVEMENT")
    public Long kickbackDuration;
    public Float recoveryRate;
    public Long recoveryDuration;

    private enum RecoilType {
        CAMERA_MOVEMENT, RANDOM_SPREAD
    }
}
