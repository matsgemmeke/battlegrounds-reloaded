package nl.matsgemmeke.battlegrounds.item.projectile;

import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class ItemProjectile implements Projectile, ItemEffectSource, Removable {

    private final Item item;

    public ItemProjectile(Item item) {
        this.item = item;
    }

    @Override
    public Location getLocation() {
        return item.getLocation();
    }

    @Override
    public Vector getVelocity() {
        return item.getVelocity();
    }

    @Override
    public void setVelocity(Vector velocity) {
        item.setVelocity(velocity);
    }

    @Override
    public World getWorld() {
        return item.getWorld();
    }

    @Override
    public boolean hasGravity() {
        return item.hasGravity();
    }

    @Override
    public void setGravity(boolean gravity) {
        item.setGravity(gravity);
    }

    @Override
    public boolean exists() {
        return !item.isDead();
    }

    @Override
    public void remove() {
        item.remove();
    }
}
