package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.HexColor;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class DustOptionsSpec {

    @Required
    @HexColor
    public String color;

    @Required
    public Float size;
}
