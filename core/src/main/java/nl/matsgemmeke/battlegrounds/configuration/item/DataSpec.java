package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DataSpec {

    @Required
    public String key;

    @Required
    @EnumValue(type = DataType.class)
    public String type;

    @Required
    public String value;
}
