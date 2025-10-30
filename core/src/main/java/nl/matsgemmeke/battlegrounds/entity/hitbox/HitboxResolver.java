package nl.matsgemmeke.battlegrounds.entity.hitbox;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.HumanoidHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.PlayerHitbox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HitboxResolver {

    private static final Map<EntityType, HitboxFactory<? extends Entity>> hitboxFactories = new HashMap<>();

    static {
        hitboxFactories.put(EntityType.PLAYER, entity -> new PlayerHitbox((Player) entity));
        hitboxFactories.put(EntityType.ZOMBIE, entity -> new HumanoidHitbox((Zombie) entity));
    }

    private final HitboxConfiguration hitboxConfiguration;

    @Inject
    public HitboxResolver(HitboxConfiguration hitboxConfiguration) {
        this.hitboxConfiguration = hitboxConfiguration;
    }

    @SuppressWarnings("unchecked")
    public Optional<Hitbox> resolveHitbox(Entity entity) {
        var hitboxFactory = hitboxFactories.get(entity.getType());

        if (hitboxFactory == null) {
            return Optional.empty();
        }

        HitboxFactory<Entity> factory = (HitboxFactory<Entity>) hitboxFactory;

        return Optional.of(factory.create(entity));
    }
}
