package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ReloadingSpec {

    @Required
    public String type;
    @Required
    public Integer duration;
    public String reloadSounds;
}
