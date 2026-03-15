package nl.matsgemmeke.battlegrounds.configuration.item.projectile;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

import java.util.Map;

public abstract class ProjectileSpec {

    @Required
    @EnumValue(type = ProjectileType.class)
    public String type;

    @Required
    @Valid
    public ItemEffectSpec effect;

    @Valid
    public ParticleEffectSpec trajectoryParticleEffect;

    public String launchSounds;

    public Map<String, TriggerSpec> triggers;

    private enum ProjectileType {
        ARROW, FIREBALL, HITSCAN, ITEM
    }
}
