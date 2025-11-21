package nl.matsgemmeke.battlegrounds.entity.hitbox;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.*;
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
        hitboxProviders.put(EntityType.ENDERMAN, this::createEndermanHitboxProvider);
        hitboxProviders.put(EntityType.PLAYER, this::createPlayerHitboxProvider);
        hitboxProviders.put(EntityType.SKELETON, () -> this.createDefaultHitboxProvider("skeleton", "standing", HitboxDefaults.SKELETON_STANDING));
        hitboxProviders.put(EntityType.SPIDER, () -> this.createDefaultHitboxProvider("spider", "standing", HitboxDefaults.SPIDER_STANDING));
        hitboxProviders.put(EntityType.ZOMBIE, this::createZombieHitboxProvider);
    }

    public Optional<HitboxProvider> resolveHitboxProvider(Entity entity) {
        Supplier<HitboxProvider> hitboxProvider = hitboxProviders.get(entity.getType());

        if (hitboxProvider == null) {
            return Optional.empty();
        }

        return Optional.of(hitboxProvider.get());
    }

    private HitboxProvider createEndermanHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("enderman", "standing", HitboxDefaults.ENDERMAN_STANDING);
        RelativeHitbox carryingHitbox = this.createRelativeHitbox("enderman", "carrying", HitboxDefaults.ENDERMAN_CARRYING);

        return new EndermanHitboxProvider(standingHitbox, carryingHitbox);
    }

    private HitboxProvider createPlayerHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("player", "standing", HitboxDefaults.PLAYER_STANDING);
        RelativeHitbox sneakingHitbox = this.createRelativeHitbox("player", "sneaking", HitboxDefaults.PLAYER_SNEAKING);
        RelativeHitbox sleepingHitbox = this.createRelativeHitbox("player", "sleeping", HitboxDefaults.PLAYER_SLEEPING);

        return new PlayerHitboxProvider(standingHitbox, sneakingHitbox, sleepingHitbox);
    }

    private HitboxProvider createDefaultHitboxProvider(String entityType, String pose, RelativeHitbox defaultHitbox) {
        RelativeHitbox standingHitbox = this.createRelativeHitbox(entityType, pose, defaultHitbox);

        return new DefaultHitboxProvider(standingHitbox);
    }

    private HitboxProvider createZombieHitboxProvider() {
        RelativeHitbox standingHitboxAdult = this.createRelativeHitbox("zombie", "adult-standing", HitboxDefaults.ZOMBIE_ADULT_STANDING);
        RelativeHitbox standingHitboxBaby = this.createRelativeHitbox("zombie", "baby-standing", HitboxDefaults.ZOMBIE_BABY_STANDING);

        return new ZombieHitboxProvider(standingHitboxAdult, standingHitboxBaby);
    }

    private RelativeHitbox createRelativeHitbox(String entityType, String pose, RelativeHitbox defaultHitbox) {
        HitboxDefinition hitboxDefinition = hitboxConfiguration.getHitboxDefinition(entityType, pose).orElse(null);

        return hitboxDefinition != null ? hitboxMapper.map(hitboxDefinition) : defaultHitbox;
    }
}
