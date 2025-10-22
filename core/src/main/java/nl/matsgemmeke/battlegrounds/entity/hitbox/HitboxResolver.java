package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HitboxResolver {

    private static final Map<EntityType, Hitbox> hitboxes = new HashMap<>();

    static {
        hitboxes.put(EntityType.PLAYER, new HumanoidHitbox());
    }

    public Optional<HitboxPart> resolveHitboxPart(Entity entity, Location hitLocation) {
        Hitbox hitbox = hitboxes.get(entity.getType());

        if (hitbox == null || !hitbox.intersects(hitLocation)) {
            return Optional.empty();
        }

        return Optional.of(HitboxPart.BODY);
    }
}
