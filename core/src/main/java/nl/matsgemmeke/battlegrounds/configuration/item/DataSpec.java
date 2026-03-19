package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class DataSpec {

    @Required
    public String key;

    @Required
    @EnumValue(type = DataType.class)
    public String type;

    @Required
    public String value;
}
