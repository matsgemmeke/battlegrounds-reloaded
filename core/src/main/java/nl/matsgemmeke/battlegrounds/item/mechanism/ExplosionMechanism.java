package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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

    public void activate(@Nullable Item droppedItem, @NotNull ItemHolder holder) {
        Entity source;
        Location location;
        World world;

        if (droppedItem != null) {
            source = droppedItem;
            location = droppedItem.getLocation();
            world = droppedItem.getWorld();
            droppedItem.remove();
        } else {
            source = holder.getEntity();
            location = holder.getEntity().getLocation();
            world = holder.getEntity().getWorld();
        }

        world.createExplosion(location, power, setFire, breakBlocks, source);

        this.inflictDamage(holder, location);
    }

    private void inflictDamage(@NotNull ItemHolder holder, @NotNull Location location) {
        for (GameEntity target : collisionDetector.findTargets(holder, location, rangeProfile.getLongRangeDistance())) {
            Location targetLocation = target.getEntity().getLocation();

            double distance = location.distance(targetLocation);
            double damage = rangeProfile.getDamageByDistance(distance);

            target.damage(damage);
        }
    }
}
