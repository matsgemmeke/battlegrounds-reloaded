package nl.matsgemmeke.battlegrounds.entity.hitbox;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.PlayerHitboxProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.ZombieHitboxProvider;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HitboxResolver {

    private final HitboxConfiguration hitboxConfiguration;
    private final HitboxMapper hitboxMapper;
    private final Map<EntityType, Supplier<HitboxProvider>> hitboxProviders;

    @Inject
    public HitboxResolver(HitboxConfiguration hitboxConfiguration, HitboxMapper hitboxMapper) {
        this.hitboxConfiguration = hitboxConfiguration;
        this.hitboxMapper = hitboxMapper;
        this.hitboxProviders = new HashMap<>();

        this.registerHitboxProviders();
    }

    public void registerHitboxProviders() {
        hitboxProviders.put(EntityType.PLAYER, this::createPlayerHitboxProvider);
        hitboxProviders.put(EntityType.ZOMBIE, this::createZombieHitboxProvider);
    }

    public Optional<HitboxProvider> resolveHitboxProvider(Entity entity) {
        Supplier<HitboxProvider> hitboxProvider = hitboxProviders.get(entity.getType());

        if (hitboxProvider == null) {
            return Optional.empty();
        }

        return Optional.of(hitboxProvider.get());
    }

    private HitboxProvider createPlayerHitboxProvider() {
        HitboxDefinition standingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("player", "standing").orElse(null);
        HitboxDefinition sneakingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("player", "sneaking").orElse(null);

        RelativeHitbox standingHitbox;
        RelativeHitbox sneakingHitbox;

        if (standingHitboxDefinition != null) {
            standingHitbox = hitboxMapper.map(standingHitboxDefinition);
        } else {
            standingHitbox = HitboxDefaults.PLAYER_STANDING;
        }

        if (sneakingHitboxDefinition != null) {
            sneakingHitbox = hitboxMapper.map(sneakingHitboxDefinition);
        } else {
            sneakingHitbox = HitboxDefaults.PLAYER_SNEAKING;
        }

        return new PlayerHitboxProvider(standingHitbox, sneakingHitbox);
    }

    private HitboxProvider createZombieHitboxProvider() {
        HitboxDefinition adultStandingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("zombie", "adult-standing").orElse(null);
        HitboxDefinition babyStandingHitboxDefinition = hitboxConfiguration.getHitboxDefinition("zombie", "baby-standing").orElse(null);

        RelativeHitbox standingHitboxAdult;
        RelativeHitbox standingHitboxBaby;

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
}
