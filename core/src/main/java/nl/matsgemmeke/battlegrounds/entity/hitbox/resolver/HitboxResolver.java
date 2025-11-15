package nl.matsgemmeke.battlegrounds.entity.hitbox.resolver;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxDefaults;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.HumanoidHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.PlayerHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.PlayerHitboxProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.ZombieHitboxProvider;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HitboxResolver {

    private final HitboxConfiguration hitboxConfiguration;
    private final HitboxMapper hitboxMapper;
    private final Map<EntityType, HitboxFactory> hitboxFactories;
    private final Map<EntityType, HitboxProvider> hitboxProviders;

    @Inject
    public HitboxResolver(HitboxConfiguration hitboxConfiguration, HitboxMapper hitboxMapper) {
        this.hitboxConfiguration = hitboxConfiguration;
        this.hitboxMapper = hitboxMapper;
        this.hitboxFactories = new HashMap<>();
        this.hitboxProviders = new HashMap<>();

        this.registerHitboxFactories();
        this.registerHitboxProviders();
    }

    public void registerHitboxFactories() {
        hitboxFactories.put(EntityType.PLAYER, entity -> this.createPlayerHitbox((Player) entity));
        hitboxFactories.put(EntityType.ZOMBIE, entity -> this.createZombieHitbox((Zombie) entity));
    }

    private void registerHitboxProviders() {
        hitboxProviders.put(EntityType.PLAYER, this.createPlayerHitboxProvider());
        hitboxProviders.put(EntityType.ZOMBIE, this.createZombieHitboxProvider());
    }

    public Optional<Hitbox> resolveHitbox(Entity entity) {
        HitboxFactory hitboxFactory = hitboxFactories.get(entity.getType());

        if (hitboxFactory == null) {
            return Optional.empty();
        }

        return Optional.of(hitboxFactory.create(entity));
    }

    public Optional<HitboxProvider> resolveHitboxProvider(Entity entity) {
        HitboxProvider hitboxProvider = hitboxProviders.get(entity.getType());

        if (hitboxProvider == null) {
            return Optional.empty();
        }

        return Optional.of(hitboxProvider);
    }

    private Hitbox createPlayerHitbox(Player player) {
        HitboxDefinition standingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("player", "standing").orElse(null);
        PositionHitbox standingHitbox;

        if (standingHitboxDefinition != null) {
            standingHitbox = hitboxMapper.map(standingHitboxDefinition);
        } else {
            standingHitbox = HitboxDefaults.PLAYER_STANDING;
        }

        return new PlayerHitbox(player, standingHitbox);
    }

    private HitboxProvider createPlayerHitboxProvider() {
        HitboxDefinition standingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("player", "standing").orElse(null);

        PositionHitbox standingHitbox;

        if (standingHitboxDefinition != null) {
            standingHitbox = hitboxMapper.map(standingHitboxDefinition);
        } else {
            standingHitbox = HitboxDefaults.PLAYER_STANDING;
        }

        return new PlayerHitboxProvider(standingHitbox);
    }

    private HitboxProvider createZombieHitboxProvider() {
        HitboxDefinition adultStandingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("zombie", "adult-standing").orElse(null);
        HitboxDefinition babyStandingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("zombie", "baby-standing").orElse(null);

        PositionHitbox standingHitboxAdult;
        PositionHitbox standingHitboxBaby;

        if (adultStandingHitboxDefinition != null) {
            standingHitboxAdult = hitboxMapper.map(adultStandingHitboxDefinition);
        } else {
            standingHitboxAdult = HitboxDefaults.ZOMBIE_ADULT_STANDING;
        }

        if (babyStandingHitboxDefinition != null) {
            standingHitboxBaby = hitboxMapper.map(babyStandingHitboxDefinition);
        } else {
            standingHitboxBaby = HitboxDefaults.ZOMBIE_BABY_STANDING;
        }

        return new ZombieHitboxProvider(standingHitboxAdult, standingHitboxBaby);
    }

    private Hitbox createZombieHitbox(Zombie zombie) {
        HitboxDefinition standingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("zombie", "adult-standing").orElse(null);
        PositionHitbox standingHitbox;

        if (standingHitboxDefinition != null) {
            standingHitbox = hitboxMapper.map(standingHitboxDefinition);
        } else {
            standingHitbox = HitboxDefaults.ZOMBIE_ADULT_STANDING;
        }

        return new HumanoidHitbox(zombie, standingHitbox);
    }
}
