package nl.matsgemmeke.battlegrounds.entity.hitbox.impl;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;

public class HumanoidHitbox implements Hitbox {

    private final LivingEntity entity;

    public HumanoidHitbox(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public Optional<HitboxComponentType> getHitboxComponentType(Location location) {
        Location playerLocation = entity.getLocation();

        if (playerLocation.getWorld() != location.getWorld()) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public boolean intersects(Location location) {
        Location entityLocation = entity.getLocation();

        if (entityLocation.getWorld() != location.getWorld()) {
            return false;
        }

        if (entity instanceof Ageable && !((Ageable) entity).isAdult()) {
//            return HitboxUtil.intersectsBox(location, entityLocation, HEAD_HEIGHT_BABY, WIDTH_BABY);
        } else {
//            return HitboxUtil.intersectsBox(location, entityLocation, HEAD_HEIGHT_ADULT, WIDTH_ADULT);
        }

        return false;
    }
}
