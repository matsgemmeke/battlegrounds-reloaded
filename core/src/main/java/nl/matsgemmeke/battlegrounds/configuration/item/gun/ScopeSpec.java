package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class ScopeSpec {

    @Required
    public Float[] magnifications;

    public String useSounds;

    public String stopSounds;

    public String changeMagnificationSounds;
}
