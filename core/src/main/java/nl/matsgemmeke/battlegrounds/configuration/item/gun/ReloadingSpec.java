package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ReloadingSpec {

    @Required
    @EnumValue(ReloadType.class)
    public String type;
    @Required
    public Integer duration;
    public String reloadSounds;

    private enum ReloadType {
        MAGAZINE, MANUAL_INSERTION
    }
}
