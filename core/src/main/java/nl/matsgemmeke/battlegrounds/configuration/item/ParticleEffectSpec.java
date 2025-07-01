package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import org.bukkit.Material;
import org.bukkit.Particle;

public class ParticleEffectSpec {

    @Required
    @EnumValue(Particle.class)
    public String particle;
    @Required
    public Integer count;
    @Required
    public Double offsetX;
    @Required
    public Double offsetY;
    @Required
    public Double offsetZ;
    @Required
    public Double extra;
    @EnumValue(Material.class)
    public String blockData;
    public DustOptionsSpec dustOptions;
}
