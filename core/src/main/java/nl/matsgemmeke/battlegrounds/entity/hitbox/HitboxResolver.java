package nl.matsgemmeke.battlegrounds.entity.hitbox;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.*;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxDefaults.*;

public class HitboxResolver {

    private static final HitboxProviderNew<Entity> FALLBACK_PROVIDER = new BoundingBoxHitboxProvider();

    private final HitboxConfiguration hitboxConfiguration;
    private final HitboxMapper hitboxMapper;
    private final Map<EntityType, Supplier<HitboxProvider>> hitboxProviders;
    private final Map<EntityType, Supplier<HitboxProviderNew<? extends Entity>>> entityHitboxProviders;

    @Inject
    public HitboxResolver(HitboxConfiguration hitboxConfiguration, HitboxMapper hitboxMapper) {
        this.hitboxConfiguration = hitboxConfiguration;
        this.hitboxMapper = hitboxMapper;
        this.hitboxProviders = new HashMap<>();
        this.entityHitboxProviders = new HashMap<>();

        this.registerHitboxProviders();
        this.registerEntityHitboxProviders();
    }

    private void registerHitboxProviders() {
        hitboxProviders.put(EntityType.CHICKEN, () -> this.createAgeableHitboxProvider("chicken", "adult-standing", "baby-standing", CHICKEN_ADULT_STANDING, CHICKEN_BABY_STANDING));
        hitboxProviders.put(EntityType.COW, () -> this.createAgeableHitboxProvider("cow", "adult-standing", "baby-standing", COW_ADULT_STANDING, COW_BABY_STANDING));
        hitboxProviders.put(EntityType.CREEPER, () -> this.createDefaultHitboxProvider("creeper", "standing", CREEPER_STANDING));
        hitboxProviders.put(EntityType.IRON_GOLEM, () -> this.createDefaultHitboxProvider("iron-golem", "standing", IRON_GOLEM_STANDING));
        hitboxProviders.put(EntityType.PIG, () -> this.createAgeableHitboxProvider("pig", "adult-standing", "baby-standing", PIG_ADULT_STANDING, PIG_BABY_STANDING));
        hitboxProviders.put(EntityType.SHEEP, () -> this.createAgeableHitboxProvider("sheep", "adult-standing", "baby-standing", SHEEP_ADULT_STANDING, SHEEP_BABY_STANDING));
        hitboxProviders.put(EntityType.SKELETON, () -> this.createDefaultHitboxProvider("skeleton", "standing", SKELETON_STANDING));
        hitboxProviders.put(EntityType.SPIDER, () -> this.createDefaultHitboxProvider("spider", "standing", SPIDER_STANDING));
        hitboxProviders.put(EntityType.ZOMBIE, () -> this.createAgeableHitboxProvider("zombie", "adult-standing", "baby-standing", ZOMBIE_ADULT_STANDING, ZOMBIE_BABY_STANDING));
    }

    private void registerEntityHitboxProviders() {
        entityHitboxProviders.put(EntityType.ENDERMAN, this::createEndermanHitboxProvider);
        entityHitboxProviders.put(EntityType.PLAYER, this::createPlayerHitboxProvider);
        entityHitboxProviders.put(EntityType.SLIME, this::createSlimeHitboxProvider);
        entityHitboxProviders.put(EntityType.VILLAGER, this::createVillagerHitboxProvider);
        entityHitboxProviders.put(EntityType.WOLF, this::createWolfHitboxProvider);
    }

    public HitboxProvider resolveHitboxProvider(Entity entity) {
        Supplier<HitboxProvider> hitboxProvider = hitboxProviders.get(entity.getType());

        if (hitboxProvider == null) {
            return null;
        }

        return hitboxProvider.get();
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> HitboxProviderNew<T> resolveHitboxProviderNew(T entity) {
        if (!entityHitboxProviders.containsKey(entity.getType())) {
            return (HitboxProviderNew<T>) FALLBACK_PROVIDER;
        }

        var hitboxProviderSupplier = entityHitboxProviders.get(entity.getType());
        return (HitboxProviderNew<T>) hitboxProviderSupplier.get();
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

    private HitboxProviderNew<Enderman> createEndermanHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("enderman", "standing", ENDERMAN_STANDING);
        RelativeHitbox carryingHitbox = this.createRelativeHitbox("enderman", "carrying", ENDERMAN_CARRYING);

        return new EndermanHitboxProvider(standingHitbox, carryingHitbox);
    }

    private HitboxProviderNew<Player> createPlayerHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("player", "standing", PLAYER_STANDING);
        RelativeHitbox sneakingHitbox = this.createRelativeHitbox("player", "sneaking", PLAYER_SNEAKING);
        RelativeHitbox sleepingHitbox = this.createRelativeHitbox("player", "sleeping", PLAYER_SLEEPING);

        return new PlayerHitboxProvider(standingHitbox, sneakingHitbox, sleepingHitbox);
    }

    private HitboxProviderNew<Slime> createSlimeHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("slime", "standing", SLIME_STANDING);

        return new SlimeHitboxProvider(standingHitbox);
    }

    private HitboxProviderNew<Villager> createVillagerHitboxProvider() {
        RelativeHitbox adultStandingHitbox = this.createRelativeHitbox("villager", "adult-standing", VILLAGER_ADULT_STANDING);
        RelativeHitbox adultSleepingHitbox = this.createRelativeHitbox("villager", "adult-sleeping", VILLAGER_ADULT_SLEEPING);
        RelativeHitbox babyStandingHitbox = this.createRelativeHitbox("villager", "baby-standing", VILLAGER_BABY_STANDING);
        RelativeHitbox babySleepingHitbox = this.createRelativeHitbox("villager", "baby-sleeping", VILLAGER_BABY_SLEEPING);

        return new VillagerHitboxProvider(adultStandingHitbox, adultSleepingHitbox, babyStandingHitbox, babySleepingHitbox);
    }

    private HitboxProviderNew<Wolf> createWolfHitboxProvider() {
        RelativeHitbox adultStandingHitbox = this.createRelativeHitbox("wolf", "adult-standing", WOLF_ADULT_STANDING);
        RelativeHitbox adultSittingHitbox = this.createRelativeHitbox("wolf", "adult-sitting", WOLF_ADULT_SITTING);
        RelativeHitbox babyStandingHitbox = this.createRelativeHitbox("wolf", "baby-standing", WOLF_BABY_STANDING);
        RelativeHitbox babySittingHitbox = this.createRelativeHitbox("wolf", "baby-sitting", WOLF_BABY_SITTING);

        return new SittableAgeableHitboxProvider<>(adultStandingHitbox, adultSittingHitbox, babyStandingHitbox, babySittingHitbox);
    }

    private RelativeHitbox createRelativeHitbox(String entityType, String pose, RelativeHitbox defaultHitbox) {
        HitboxDefinition hitboxDefinition = hitboxConfiguration.getHitboxDefinition(entityType, pose).orElse(null);

        return hitboxDefinition != null ? hitboxMapper.map(hitboxDefinition) : defaultHitbox;
    }
}
