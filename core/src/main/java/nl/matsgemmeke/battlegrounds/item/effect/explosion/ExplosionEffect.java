package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ExplosionEffect implements ItemEffect {

    @NotNull
    private ExplosionSettings settings;
    @NotNull
    private RangeProfile rangeProfile;
    @NotNull
    private TargetFinder targetFinder;

    public ExplosionEffect(
            @NotNull ExplosionSettings settings,
            @NotNull RangeProfile rangeProfile,
            @NotNull TargetFinder targetFinder
    ) {
        this.settings = settings;
        this.targetFinder = targetFinder;
        this.rangeProfile = rangeProfile;
    }

    public void activate(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        Location location = source.getLocation();
        World world = source.getWorld();
        Entity damageSource = holder.getEntity();

        world.createExplosion(location, settings.power(), settings.setFire(), settings.breakBlocks(), damageSource);

        source.remove();

        for (GameEntity target : targetFinder.findTargets(holder, location, rangeProfile.getLongRangeDistance())) {
            Location targetLocation = target.getEntity().getLocation();

            double distance = location.distance(targetLocation);
            double damage = rangeProfile.getDamageByDistance(distance);

            target.damage(damage);
        }
    }

    public void activate(@NotNull ItemEffectContext context) {
        this.activate(context.getHolder(), context.getSource());
    }
}
