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
    private ExplosionProperties properties;
    @NotNull
    private RangeProfile rangeProfile;
    @NotNull
    private TargetFinder targetFinder;

    public ExplosionEffect(@NotNull ExplosionProperties properties, @NotNull RangeProfile rangeProfile, @NotNull TargetFinder targetFinder) {
        this.properties = properties;
        this.targetFinder = targetFinder;
        this.rangeProfile = rangeProfile;
    }

    public void activate(@NotNull ItemEffectContext context) {
        ItemHolder holder = context.getHolder();
        EffectSource source = context.getSource();
        Location sourceLocation = source.getLocation();
        World world = source.getWorld();
        Entity damageSource = holder.getEntity();

        world.createExplosion(sourceLocation, properties.power(), properties.setFire(), properties.breakBlocks(), damageSource);

        source.remove();

        for (GameEntity target : targetFinder.findTargets(holder, sourceLocation, rangeProfile.getLongRangeDistance())) {
            Location targetLocation = target.getEntity().getLocation();

            double distance = sourceLocation.distance(targetLocation);
            double damage = rangeProfile.getDamageByDistance(distance);

            target.damage(damage);
        }
    }
}
