package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.List;

public class TriggerSpec {

    @Required
    @EnumValue(type = TriggerType.class)
    public String type;

    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "BLOCK_IMPACT", "ENEMY_PROXIMITY", "FLOOR_HIT" })
    public Long delay;

    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "BLOCK_IMPACT", "ENEMY_PROXIMITY", "FLOOR_HIT" })
    public Long interval;

    @ConditionalRequired(conditionalFieldName = "type", matchValues = "SCHEDULED")
    public List<Long> offsetDelays;

    @ConditionalRequired(conditionalFieldName = "type", matchValues = "ENEMY_PROXIMITY")
    public Double range;

    public Boolean repeating;

    private enum TriggerType {
        BLOCK_IMPACT, ENEMY_PROXIMITY, FLOOR_HIT, SCHEDULED
    }
}
