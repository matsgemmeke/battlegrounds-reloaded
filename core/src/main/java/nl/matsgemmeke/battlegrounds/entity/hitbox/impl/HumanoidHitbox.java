package nl.matsgemmeke.battlegrounds.entity.hitbox.impl;

import nl.matsgemmeke.battlegrounds.entity.hitbox.*;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;

public class HumanoidHitbox implements Hitbox {

    private final LivingEntity entity;
    private final PositionHitbox standingHitbox;

    public HumanoidHitbox(LivingEntity entity, PositionHitbox standingHitbox) {
        this.entity = entity;
        this.standingHitbox = standingHitbox;
    }

    @Override
    public Optional<HitboxComponentType> getHitboxComponentType(Location location) {
        Location playerLocation = entity.getLocation();

        if (playerLocation.getWorld() != location.getWorld()) {
            return Optional.empty();
        }

        HitboxComponent intersectedComponent = HitboxUtil.getIntersectedHitboxComponent(location, playerLocation, standingHitbox).orElse(null);

        if (intersectedComponent == null) {
            return Optional.empty();
        }

        return Optional.of(intersectedComponent.type());
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

        return HitboxUtil.intersectsHitbox(location, entityLocation, standingHitbox);
    }
}
