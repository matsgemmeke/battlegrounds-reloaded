package nl.matsgemmeke.battlegrounds.entity.hitbox.impl;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxPart;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlayerHitbox implements Hitbox {

    private static final double BODY_HEIGHT_UPRIGHT = 1.4;
    private static final double HEAD_HEIGHT_UPRIGHT = 1.8;
    private static final double LEGS_HEIGHT_UPRIGHT = 0.7;

    private static final double BODY_HEIGHT_SNEAKING = 1.1;
    private static final double HEAD_HEIGHT_SNEAKING = 1.5;
    private static final double LEGS_HEIGHT_SNEAKING = 0.7;

    private static final double WIDTH = 0.6;

    private static final double HEIGHT_SLEEPING = 0.2;
    private static final double WIDTH_SLEEPING = 1.8;

    private final Player player;

    public PlayerHitbox(Player player) {
        this.player = player;
    }

    @Override
    public Optional<HitboxPart> getHitPart(Location location) {
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
            return HitboxUtil.intersectsBox(location, playerLocation, HEIGHT_SLEEPING, WIDTH_SLEEPING);
        }

        if (player.isSneaking()) {
            return HitboxUtil.intersectsBox(location, playerLocation, HEAD_HEIGHT_SNEAKING, WIDTH);
        }

        return HitboxUtil.intersectsBox(location, playerLocation, HEAD_HEIGHT_UPRIGHT, WIDTH);
    }
}
