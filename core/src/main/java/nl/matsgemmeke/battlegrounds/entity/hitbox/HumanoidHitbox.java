package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public class HumanoidHitbox implements Hitbox {

    private static final double BODY_HEIGHT_ADULT = 1.4;
    private static final double BODY_HEIGHT_BABY = 0.6;
    private static final double HEAD_HEIGHT_ADULT = 1.8;
    private static final double HEAD_HEIGHT_BABY = 1;
    private static final double WIDTH_ADULT = 0.6;
    private static final double WIDTH_BABY = 0.6;

    private final Entity entity;

    public HumanoidHitbox(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean intersects(Location location) {
        Location entityLocation = entity.getLocation();

        if (entityLocation.getWorld() != location.getWorld()) {
            return false;
        }

        if (entity instanceof Ageable && !((Ageable) entity).isAdult()) {
            return HitboxUtil.intersectsBox(location, entityLocation, HEAD_HEIGHT_BABY, WIDTH_BABY);
        } else {
            return HitboxUtil.intersectsBox(location, entityLocation, HEAD_HEIGHT_ADULT, WIDTH_ADULT);
        }
    }
}
