package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DustOptionsSpec {

    @Required
    public String color;
    @Required
    public Float size;
}
