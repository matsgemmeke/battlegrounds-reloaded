package nl.matsgemmeke.battlegrounds.configuration.hitbox.definition;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.List;

public class HitboxDefinition {

    @Required
    public List<HitboxComponentDefinition> components;
}
