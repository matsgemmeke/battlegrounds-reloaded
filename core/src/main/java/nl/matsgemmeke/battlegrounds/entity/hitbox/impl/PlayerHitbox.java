package nl.matsgemmeke.battlegrounds.entity.hitbox.impl;

import nl.matsgemmeke.battlegrounds.entity.hitbox.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlayerHitbox implements Hitbox {

    private final Player player;
    private final PositionHitbox standingHitbox;

    public PlayerHitbox(Player player, PositionHitbox standingHitbox) {
        this.player = player;
        this.standingHitbox = standingHitbox;
    }

    @Override
    public PositionHitbox getCurrentPositionHitbox() {
        return standingHitbox;
    }

    @Override
    public Optional<HitboxComponentType> getHitboxComponentType(Location location) {
        Location playerLocation = player.getLocation();

        if (playerLocation.getWorld() != location.getWorld()) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public boolean intersects(Location location) {
        Location playerLocation = player.getLocation();

        if (playerLocation.getWorld() != location.getWorld()) {
            return false;
        }

        if (player.isSleeping()) {
//            return HitboxUtil.intersectsBox(location, playerLocation, HEIGHT_SLEEPING, WIDTH_SLEEPING);
        }

        if (player.isSneaking()) {
//            return HitboxUtil.intersectsBox(location, playerLocation, HEAD_HEIGHT_SNEAKING, WIDTH);
        }

        return HitboxUtil.intersectsHitbox(location, playerLocation, standingHitbox);
    }
}
