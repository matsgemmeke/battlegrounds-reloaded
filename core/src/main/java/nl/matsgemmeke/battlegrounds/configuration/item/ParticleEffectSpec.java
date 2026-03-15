package nl.matsgemmeke.battlegrounds.configuration.item;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;
import org.bukkit.Material;
import org.bukkit.Particle;

public class ParticleEffectSpec {

    @Required
    @EnumValue(type = Particle.class)
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

    @EnumValue(type = Material.class)
    public String blockData;

    @Valid
    public DustOptionsSpec dustOptions;
}
