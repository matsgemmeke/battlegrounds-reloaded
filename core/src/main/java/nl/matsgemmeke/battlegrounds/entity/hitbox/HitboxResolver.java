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

    private static final HitboxProvider FALLBACK_PROVIDER = new BoundingBoxHitboxProvider();

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
        hitboxProviders.put(EntityType.CHICKEN, () -> this.createAgeableHitboxProvider("chicken", "adult-standing", "baby-standing", CHICKEN_ADULT_STANDING, CHICKEN_BABY_STANDING));
        hitboxProviders.put(EntityType.COW, () -> this.createAgeableHitboxProvider("cow", "adult-standing", "baby-standing", COW_ADULT_STANDING, COW_BABY_STANDING));
        hitboxProviders.put(EntityType.CREEPER, () -> this.createDefaultHitboxProvider("creeper", "standing", CREEPER_STANDING));
        hitboxProviders.put(EntityType.ENDERMAN, this::createEndermanHitboxProvider);
        hitboxProviders.put(EntityType.IRON_GOLEM, () -> this.createDefaultHitboxProvider("iron-golem", "standing", IRON_GOLEM_STANDING));
        hitboxProviders.put(EntityType.PIG, () -> this.createAgeableHitboxProvider("pig", "adult-standing", "baby-standing", PIG_ADULT_STANDING, PIG_BABY_STANDING));
        hitboxProviders.put(EntityType.PLAYER, this::createPlayerHitboxProvider);
        hitboxProviders.put(EntityType.SHEEP, () -> this.createAgeableHitboxProvider("sheep", "adult-standing", "baby-standing", SHEEP_ADULT_STANDING, SHEEP_BABY_STANDING));
        hitboxProviders.put(EntityType.SKELETON, () -> this.createDefaultHitboxProvider("skeleton", "standing", SKELETON_STANDING));
        hitboxProviders.put(EntityType.SLIME, this::createSlimeHitboxProvider);
        hitboxProviders.put(EntityType.SPIDER, () -> this.createDefaultHitboxProvider("spider", "standing", SPIDER_STANDING));
        hitboxProviders.put(EntityType.VILLAGER, this::createVillagerHitboxProvider);
        hitboxProviders.put(EntityType.WOLF, () -> this.createSittableHitboxProvider("wolf", "adult-standing", "adult-sitting", "baby-standing", "baby-sitting", WOLF_ADULT_STANDING, WOLF_ADULT_SITTING, WOLF_BABY_STANDING, WOLF_BABY_SITTING));
        hitboxProviders.put(EntityType.ZOMBIE, () -> this.createAgeableHitboxProvider("zombie", "adult-standing", "baby-standing", ZOMBIE_ADULT_STANDING, ZOMBIE_BABY_STANDING));
    }

    public HitboxProvider resolveHitboxProvider(Entity entity) {
        Supplier<HitboxProvider> hitboxProvider = hitboxProviders.get(entity.getType());

        if (hitboxProvider == null) {
            return FALLBACK_PROVIDER;
        }

        return hitboxProvider.get();
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
        RelativeHitbox standingHitbox = this.createRelativeHitbox("enderman", "standing", ENDERMAN_STANDING);
        RelativeHitbox carryingHitbox = this.createRelativeHitbox("enderman", "carrying", ENDERMAN_CARRYING);

        return new EndermanHitboxProvider(standingHitbox, carryingHitbox);
    }

    private HitboxProvider createPlayerHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("player", "standing", PLAYER_STANDING);
        RelativeHitbox sneakingHitbox = this.createRelativeHitbox("player", "sneaking", PLAYER_SNEAKING);
        RelativeHitbox sleepingHitbox = this.createRelativeHitbox("player", "sleeping", PLAYER_SLEEPING);

        return new PlayerHitboxProvider(standingHitbox, sneakingHitbox, sleepingHitbox);
    }

    private HitboxProvider createSittableHitboxProvider(
            String entityType,
            String adultStandingPose,
            String adultSittingPose,
            String babyStandingPose,
            String babySittingPose,
            RelativeHitbox adultStandingDefaultHitbox,
            RelativeHitbox adultSittingDefaultHitbox,
            RelativeHitbox babyStandingDefaultHitbox,
            RelativeHitbox babySittingDefaultHitbox
    ) {
        RelativeHitbox adultStandingHitbox = this.createRelativeHitbox(entityType, adultStandingPose, adultStandingDefaultHitbox);
        RelativeHitbox adultSittingHitbox = this.createRelativeHitbox(entityType, adultSittingPose, adultSittingDefaultHitbox);
        RelativeHitbox babyStandingHitbox = this.createRelativeHitbox(entityType, babyStandingPose, babyStandingDefaultHitbox);
        RelativeHitbox babySittingHitbox = this.createRelativeHitbox(entityType, babySittingPose, babySittingDefaultHitbox);

        return new SittableAgeableHitboxProvider(adultStandingHitbox, adultSittingHitbox, babyStandingHitbox, babySittingHitbox);
    }

    private HitboxProvider createSlimeHitboxProvider() {
        RelativeHitbox standingHitbox = this.createRelativeHitbox("slime", "standing", SLIME_STANDING);

        return new SlimeHitboxProvider(standingHitbox);
    }

    private HitboxProvider createVillagerHitboxProvider() {
        RelativeHitbox adultStandingHitbox = this.createRelativeHitbox("villager", "adult-standing", VILLAGER_ADULT_STANDING);
        RelativeHitbox adultSleepingHitbox = this.createRelativeHitbox("villager", "adult-sleeping", VILLAGER_ADULT_SLEEPING);
        RelativeHitbox babyStandingHitbox = this.createRelativeHitbox("villager", "baby-standing", VILLAGER_BABY_STANDING);
        RelativeHitbox babySleepingHitbox = this.createRelativeHitbox("villager", "baby-sleeping", VILLAGER_BABY_SLEEPING);

        return new VillagerHitboxProvider(adultStandingHitbox, adultSleepingHitbox, babyStandingHitbox, babySleepingHitbox);
    }

    private RelativeHitbox createRelativeHitbox(String entityType, String pose, RelativeHitbox defaultHitbox) {
        HitboxDefinition hitboxDefinition = hitboxConfiguration.getHitboxDefinition(entityType, pose).orElse(null);

        return hitboxDefinition != null ? hitboxMapper.map(hitboxDefinition) : defaultHitbox;
    }
}
