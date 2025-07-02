package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.Regex;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DustOptionsSpec {

    @Required
    @Regex(pattern = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")
    public String color;
    @Required
    public Float size;
}
