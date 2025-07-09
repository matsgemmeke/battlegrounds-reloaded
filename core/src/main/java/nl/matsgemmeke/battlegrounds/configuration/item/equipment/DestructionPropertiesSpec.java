package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DestructionPropertiesSpec {

    @Required
    public Boolean activateEffect;
    @Required
    public Boolean removeDeployment;
    @Required
    public Boolean undoEffect;
    public ParticleEffectSpec particleEffect;
}
