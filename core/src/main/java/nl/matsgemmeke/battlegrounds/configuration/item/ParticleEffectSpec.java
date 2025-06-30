package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ParticleEffectSpec {

    @Required
    public String particle;
    @Required
    public Integer count;
    @Required
    public Double offsetX;
    @Required
    public Double offsetY;
    @Required
    public Double offsetZ;
    public Double extra;
    public String blockData;
    public DustOptionsSpec dustOptions;
}
