package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record ThrowDeploymentProperties(
        @NotNull ItemTemplate itemTemplate,
        @NotNull List<GameSound> throwSounds,
        @NotNull List<ProjectileEffect> projectileEffects,
        @NotNull Map<DamageType, Double> resistances,
        double health,
        double velocity
) { }
