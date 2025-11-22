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
        hitboxProviders.put(EntityType.COW, () -> this.createAgeableHitboxProvider("cow", "adult-standing", "baby-standing", HitboxDefaults.COW_ADULT_STANDING, HitboxDefaults.COW_BABY_STANDING));
        hitboxProviders.put(EntityType.CREEPER, () -> this.createDefaultHitboxProvider("creeper", "standing", HitboxDefaults.CREEPER_STANDING));
        hitboxProviders.put(EntityType.ENDERMAN, this::createEndermanHitboxProvider);
        hitboxProviders.put(EntityType.PIG, () -> this.createAgeableHitboxProvider("pig", "adult-standing", "baby-standing", HitboxDefaults.PIG_ADULT_STANDING, HitboxDefaults.PIG_BABY_STANDING));
        hitboxProviders.put(EntityType.PLAYER, this::createPlayerHitboxProvider);
        hitboxProviders.put(EntityType.SHEEP, () -> this.createAgeableHitboxProvider("sheep", "adult-standing", "baby-standing", HitboxDefaults.SHEEP_ADULT_STANDING, HitboxDefaults.SHEEP_BABY_STANDING));
        hitboxProviders.put(EntityType.SKELETON, () -> this.createDefaultHitboxProvider("skeleton", "standing", HitboxDefaults.SKELETON_STANDING));
        hitboxProviders.put(EntityType.SLIME, this::createSlimeHitboxProvider);
        hitboxProviders.put(EntityType.SPIDER, () -> this.createDefaultHitboxProvider("spider", "standing", HitboxDefaults.SPIDER_STANDING));
        hitboxProviders.put(EntityType.ZOMBIE, () -> this.createAgeableHitboxProvider("zombie", "adult-standing", "baby-standing", HitboxDefaults.ZOMBIE_ADULT_STANDING, HitboxDefaults.ZOMBIE_BABY_STANDING));
    }

    public Optional<HitboxProvider> resolveHitboxProvider(Entity entity) {
        Supplier<HitboxProvider> hitboxProvider = hitboxProviders.get(entity.getType());

        if (hitboxProvider == null) {
            return Optional.empty();
        }

        return Optional.of(hitboxProvider.get());
    }

    private HitboxProvider createAgeableHitboxProvider(String entityType, String adultPose, String babyPose, RelativeHitbox adultDefaultHitbox, RelativeHitbox babyDefaultHitbox) {
        RelativeHitbox standingHitboxAdult = this.createRelativeHitbox(entityType, adultPose, adultDefaultHitbox);
        RelativeHitbox standingHitboxBaby = this.createRelativeHitbox(entityType, babyPose, babyDefaultHitbox);

        return new AgeableHitboxProvider(standingHitboxAdult, standingHitboxBaby);
    }

    private HitboxProvider createDefaultHitboxProvider(String entityType, String pose, RelativeHitbox defaultHitbox) {
        RelativeHitbox standingHitbox = this.createRelativeHitbox(entityType, pose, defaultHitbox);

        return new DefaultHitboxProvider(standingHitbox);
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

    private HitboxProvider createSlimeHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("slime", "standing", HitboxDefaults.SLIME_STANDING);

        return new SlimeHitboxProvider(standingHitbox);
    }

    private RelativeHitbox createRelativeHitbox(String entityType, String pose, RelativeHitbox defaultHitbox) {
        HitboxDefinition hitboxDefinition = hitboxConfiguration.getHitboxDefinition(entityType, pose).orElse(null);

        return hitboxDefinition != null ? hitboxMapper.map(hitboxDefinition) : defaultHitbox;
    }
}
