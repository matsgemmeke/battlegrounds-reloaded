package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;

import java.util.List;
import java.util.Map;

public record ThrowDeploymentProperties(
        ItemTemplate itemTemplate,
        List<GameSound> throwSounds,
        List<ProjectileEffect> projectileEffects,
        Map<DamageType, Double> resistances,
        double health,
        double velocity,
        long cooldown
) { }
