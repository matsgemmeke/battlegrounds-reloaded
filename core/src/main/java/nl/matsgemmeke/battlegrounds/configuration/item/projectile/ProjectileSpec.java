package nl.matsgemmeke.battlegrounds.configuration.item.projectile;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.Map;

public abstract class ProjectileSpec {

    @Required
    @EnumValue(type = ProjectileType.class)
    public String type;

    @Required
    public ItemEffectSpec effect;

    public ParticleEffectSpec trajectoryParticleEffect;

    public String launchSounds;

    public Map<String, TriggerSpec> triggers;

    private enum ProjectileType {
        ARROW, FIREBALL, HITSCAN, ITEM
    }
}
