package nl.matsgemmeke.battlegrounds.entity.hitbox.resolver;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitboxDefaults;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.HumanoidHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.PlayerHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HitboxResolver {

    private final HitboxConfiguration hitboxConfiguration;
    private final HitboxMapper hitboxMapper;
    private final Map<EntityType, HitboxFactory> hitboxFactories;

    @Inject
    public HitboxResolver(HitboxConfiguration hitboxConfiguration, HitboxMapper hitboxMapper) {
        this.hitboxConfiguration = hitboxConfiguration;
        this.hitboxMapper = hitboxMapper;
        this.hitboxFactories = new HashMap<>();

        this.registerHitboxFactories();
    }

    public void registerHitboxFactories() {
        hitboxFactories.put(EntityType.PLAYER, entity -> this.createPlayerHitbox((Player) entity));
        hitboxFactories.put(EntityType.ZOMBIE, entity -> this.createZombieHitbox((Zombie) entity));
    }

    public Optional<Hitbox> resolveHitbox(Entity entity) {
        HitboxFactory hitboxFactory = hitboxFactories.get(entity.getType());

        if (hitboxFactory == null) {
            return Optional.empty();
        }

        return Optional.of(hitboxFactory.create(entity));
    }

    private Hitbox createPlayerHitbox(Player player) {
        HitboxDefinition standingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("player", "standing").orElse(null);

        PositionHitbox standingHitbox;

        if (standingHitboxDefinition != null) {
            standingHitbox = hitboxMapper.map(standingHitboxDefinition);
        } else {
            standingHitbox = PositionHitboxDefaults.DEFAULT_PLAYER_STANDING_HITBOX;
        }

        return new PlayerHitbox(player, standingHitbox);
    }

    private Hitbox createZombieHitbox(Zombie zombie) {
        HitboxDefinition standingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("zombie", "standing").orElse(null);

        PositionHitbox standingHitbox;

        if (standingHitboxDefinition != null) {
            standingHitbox = hitboxMapper.map(standingHitboxDefinition);
        } else {
            standingHitbox = PositionHitboxDefaults.DEFAULT_ZOMBIE_STANDING_HITBOX;
        }

        return new HumanoidHitbox(zombie, standingHitbox);
    }
}
