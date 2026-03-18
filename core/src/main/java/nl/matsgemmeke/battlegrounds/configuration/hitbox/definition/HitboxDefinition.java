package nl.matsgemmeke.battlegrounds.configuration.hitbox.definition;

import jakarta.validation.constraints.Size;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

import java.util.List;

public class HitboxDefinition {

    @Required
    @Size(min = 1)
    public List<HitboxComponentDefinition> components;
}
