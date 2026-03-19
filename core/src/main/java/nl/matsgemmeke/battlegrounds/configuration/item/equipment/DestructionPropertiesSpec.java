package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class DestructionPropertiesSpec {

    @Required
    public Boolean activateEffect;

    @Required
    public Boolean removeDeployment;

    @Required
    public Boolean undoEffect;

    @Valid
    public ParticleEffectSpec particleEffect;
}
