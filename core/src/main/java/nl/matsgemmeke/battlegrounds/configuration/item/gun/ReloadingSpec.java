package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ReloadingSpec {

    @Required
    @EnumValue(type = ReloadType.class)
    public String type;
    @Required
    public Long duration;
    public String reloadSounds;

    private enum ReloadType {
        MAGAZINE, MANUAL_INSERTION
    }
}
