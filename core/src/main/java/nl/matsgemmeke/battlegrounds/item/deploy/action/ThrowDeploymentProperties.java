package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;

import java.util.List;
import java.util.Map;

public record ThrowDeploymentProperties(
        ItemTemplate itemTemplate,
        SoundTemplate soundTemplate,
        List<ProjectileEffect> projectileEffects,
        Map<DamageType, Double> resistances,
        double health,
        double velocity,
        long cooldown
) { }
