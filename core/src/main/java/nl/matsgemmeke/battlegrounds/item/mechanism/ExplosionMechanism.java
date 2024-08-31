package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExplosionMechanism implements ItemMechanism {

    private boolean breakBlocks;
    private boolean setFire;
    @NotNull
    private CollisionDetector collisionDetector;
    private float power;
    @NotNull
    private RangeProfile rangeProfile;

    public ExplosionMechanism(
            @NotNull CollisionDetector collisionDetector,
            @NotNull RangeProfile rangeProfile,
            float power,
            boolean setFire,
            boolean breakBlocks
    ) {
        this.collisionDetector = collisionDetector;
        this.rangeProfile = rangeProfile;
        this.power = power;
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
    }

    public void activate(@NotNull ItemHolder holder) {
        this.activate(holder, holder.getLocation(), holder.getWorld(), holder.getEntity());
    }

    public void activate(@NotNull ItemHolder holder, @NotNull Deployable object) {
        this.activate(holder, object.getLocation(), object.getWorld(), object.getDamageSource());

        object.remove();
    }

    private void activate(@NotNull ItemHolder holder, @NotNull Location location, @NotNull World world, @Nullable Entity source) {
        world.createExplosion(location, power, setFire, breakBlocks, source);

        for (GameEntity target : collisionDetector.findTargets(holder, location, rangeProfile.getLongRangeDistance())) {
            Location targetLocation = target.getEntity().getLocation();

            double distance = location.distance(targetLocation);
            double damage = rangeProfile.getDamageByDistance(distance);

            target.damage(damage);
        }
    }
}
