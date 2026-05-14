package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.validation.constraint.HexColor;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class DustOptionsSpec {

    @Required
    @HexColor
    public String color;

    @Required
    public Float size;
}
