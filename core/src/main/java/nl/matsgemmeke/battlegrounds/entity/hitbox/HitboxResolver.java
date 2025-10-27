package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class HitboxResolver {

    private static final Map<EntityType, Function<Entity, Hitbox>> hitboxes = new HashMap<>();

    static {
        hitboxes.put(EntityType.PLAYER, HumanoidHitbox::new);
    }

    public Optional<HitboxPart> resolveHitboxPart(Entity entity, Location hitLocation) {
        var function = hitboxes.get(entity.getType());

        if (function == null) {
            return Optional.empty();
        }

        Hitbox hitbox = function.apply(entity);

        if (!hitbox.intersects(hitLocation)) {
            return Optional.empty();
        }

        return Optional.of(HitboxPart.BODY);
    }
}
