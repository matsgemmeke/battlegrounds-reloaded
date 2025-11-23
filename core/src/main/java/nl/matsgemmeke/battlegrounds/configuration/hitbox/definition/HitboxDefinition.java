package nl.matsgemmeke.battlegrounds.configuration.hitbox.definition;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.Size;

import java.util.List;

public class HitboxDefinition {

    @Required
    @Size(min = 1)
    public List<HitboxComponentDefinition> components;
}
