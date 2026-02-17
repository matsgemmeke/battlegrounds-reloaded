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

    private static final HitboxProvider<Entity> FALLBACK_PROVIDER = new DefaultEntityHitboxProvider();
    private static final HitboxProvider<StaticBoundingBox> DEPLOYMENT_OBJECT_HITBOX_PROVIDER = new StaticBoundingBoxHitboxProvider();

    private final HitboxConfiguration hitboxConfiguration;
    private final HitboxMapper hitboxMapper;
    private final Map<EntityType, Supplier<HitboxProvider<? extends Entity>>> entityHitboxProviders;

    @Inject
    public HitboxResolver(HitboxConfiguration hitboxConfiguration, HitboxMapper hitboxMapper) {
        this.hitboxConfiguration = hitboxConfiguration;
        this.hitboxMapper = hitboxMapper;
        this.entityHitboxProviders = new HashMap<>();

        this.registerEntityHitboxProviders();
    }

    private void registerEntityHitboxProviders() {
        entityHitboxProviders.put(EntityType.CHICKEN, () -> this.createAgeableHitboxProvider("chicken", "adult-standing", "baby-standing", CHICKEN_ADULT_STANDING, CHICKEN_BABY_STANDING));
        entityHitboxProviders.put(EntityType.COW, () -> this.createAgeableHitboxProvider("cow", "adult-standing", "baby-standing", COW_ADULT_STANDING, COW_BABY_STANDING));
        entityHitboxProviders.put(EntityType.CREEPER, () -> this.createSimpleEntityHitboxProvider("creeper", "standing", CREEPER_STANDING));
        entityHitboxProviders.put(EntityType.ENDERMAN, this::createEndermanHitboxProvider);
        entityHitboxProviders.put(EntityType.IRON_GOLEM, () -> this.createSimpleEntityHitboxProvider("iron-golem", "standing", IRON_GOLEM_STANDING));
        entityHitboxProviders.put(EntityType.PIG, () -> this.createAgeableHitboxProvider("pig", "adult-standing", "baby-standing", PIG_ADULT_STANDING, PIG_BABY_STANDING));
        entityHitboxProviders.put(EntityType.PLAYER, this::createPlayerHitboxProvider);
        entityHitboxProviders.put(EntityType.SHEEP, () -> this.createAgeableHitboxProvider("sheep", "adult-standing", "baby-standing", SHEEP_ADULT_STANDING, SHEEP_BABY_STANDING));
        entityHitboxProviders.put(EntityType.SKELETON, () -> this.createSimpleEntityHitboxProvider("skeleton", "standing", SKELETON_STANDING));
        entityHitboxProviders.put(EntityType.SLIME, this::createSlimeHitboxProvider);
        entityHitboxProviders.put(EntityType.SPIDER, () -> this.createSimpleEntityHitboxProvider("spider", "standing", SPIDER_STANDING));
        entityHitboxProviders.put(EntityType.VILLAGER, this::createVillagerHitboxProvider);
        entityHitboxProviders.put(EntityType.WOLF, this::createWolfHitboxProvider);
        entityHitboxProviders.put(EntityType.ZOMBIE, () -> this.createAgeableHitboxProvider("zombie", "adult-standing", "baby-standing", ZOMBIE_ADULT_STANDING, ZOMBIE_BABY_STANDING));
    }

    public HitboxProvider<StaticBoundingBox> resolveDeploymentObjectHitboxProvider() {
        return DEPLOYMENT_OBJECT_HITBOX_PROVIDER;
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> HitboxProvider<T> resolveHitboxProvider(T entity) {
        if (!entityHitboxProviders.containsKey(entity.getType())) {
            return (HitboxProvider<T>) FALLBACK_PROVIDER;
        }

        var hitboxProviderSupplier = entityHitboxProviders.get(entity.getType());
        return (HitboxProvider<T>) hitboxProviderSupplier.get();
    }

    private HitboxProvider<Ageable> createAgeableHitboxProvider(String entityType, String adultPose, String babyPose, RelativeHitbox adultDefaultHitbox, RelativeHitbox babyDefaultHitbox) {
        RelativeHitbox standingHitboxAdult = this.createRelativeHitbox(entityType, adultPose, adultDefaultHitbox);
        RelativeHitbox standingHitboxBaby = this.createRelativeHitbox(entityType, babyPose, babyDefaultHitbox);

        return new AgeableHitboxProvider<>(standingHitboxAdult, standingHitboxBaby);
    }

    private HitboxProvider<Enderman> createEndermanHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("enderman", "standing", ENDERMAN_STANDING);
        RelativeHitbox carryingHitbox = this.createRelativeHitbox("enderman", "carrying", ENDERMAN_CARRYING);

        return new EndermanHitboxProvider(standingHitbox, carryingHitbox);
    }

    private HitboxProvider<Player> createPlayerHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("player", "standing", PLAYER_STANDING);
        RelativeHitbox sneakingHitbox = this.createRelativeHitbox("player", "sneaking", PLAYER_SNEAKING);
        RelativeHitbox sleepingHitbox = this.createRelativeHitbox("player", "sleeping", PLAYER_SLEEPING);

        return new PlayerHitboxProvider(standingHitbox, sneakingHitbox, sleepingHitbox);
    }

    private HitboxProvider<Entity> createSimpleEntityHitboxProvider(String entityType, String pose, RelativeHitbox defaultHitbox) {
        RelativeHitbox standingHitbox = this.createRelativeHitbox(entityType, pose, defaultHitbox);

        return new SimpleEntityHitboxProvider(standingHitbox);
    }

    private HitboxProvider<Slime> createSlimeHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("slime", "standing", SLIME_STANDING);

        return new SlimeHitboxProvider(standingHitbox);
    }

    private HitboxProvider<Villager> createVillagerHitboxProvider() {
        RelativeHitbox adultStandingHitbox = this.createRelativeHitbox("villager", "adult-standing", VILLAGER_ADULT_STANDING);
        RelativeHitbox adultSleepingHitbox = this.createRelativeHitbox("villager", "adult-sleeping", VILLAGER_ADULT_SLEEPING);
        RelativeHitbox babyStandingHitbox = this.createRelativeHitbox("villager", "baby-standing", VILLAGER_BABY_STANDING);
        RelativeHitbox babySleepingHitbox = this.createRelativeHitbox("villager", "baby-sleeping", VILLAGER_BABY_SLEEPING);

        return new VillagerHitboxProvider(adultStandingHitbox, adultSleepingHitbox, babyStandingHitbox, babySleepingHitbox);
    }

    private HitboxProvider<Wolf> createWolfHitboxProvider() {
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
