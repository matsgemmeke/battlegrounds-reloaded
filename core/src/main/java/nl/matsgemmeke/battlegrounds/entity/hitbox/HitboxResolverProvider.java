package nl.matsgemmeke.battlegrounds.entity.hitbox;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxDefinitionResult;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.*;
import org.bukkit.entity.*;

import java.util.logging.Logger;

import static nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxDefaults.*;

public class HitboxResolverProvider implements Provider<HitboxResolver> {

    private final HitboxConfiguration hitboxConfiguration;
    private final HitboxMapper hitboxMapper;
    private final Logger logger;

    @Inject
    public HitboxResolverProvider(HitboxConfiguration hitboxConfiguration, HitboxMapper hitboxMapper, Logger logger) {
        this.hitboxConfiguration = hitboxConfiguration;
        this.hitboxMapper = hitboxMapper;
        this.logger = logger;
    }

    @Override
    public HitboxResolver get() {
        HitboxResolver hitboxResolver = new HitboxResolver();

        hitboxResolver.addEntityHitboxProvider(EntityType.CHICKEN, this.createAgeableHitboxProvider("chicken", "adult-standing", "baby-standing", CHICKEN_ADULT_STANDING, CHICKEN_BABY_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.COW, this.createAgeableHitboxProvider("cow", "adult-standing", "baby-standing", COW_ADULT_STANDING, COW_BABY_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.CREEPER, this.createSimpleEntityHitboxProvider("creeper", "standing", CREEPER_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.ENDERMAN, this.createEndermanHitboxProvider());
        hitboxResolver.addEntityHitboxProvider(EntityType.IRON_GOLEM, this.createSimpleEntityHitboxProvider("iron-golem", "standing", IRON_GOLEM_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.PIG, this.createAgeableHitboxProvider("pig", "adult-standing", "baby-standing", PIG_ADULT_STANDING, PIG_BABY_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.PLAYER, this.createPlayerHitboxProvider());
        hitboxResolver.addEntityHitboxProvider(EntityType.SHEEP, this.createAgeableHitboxProvider("sheep", "adult-standing", "baby-standing", SHEEP_ADULT_STANDING, SHEEP_BABY_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.SKELETON, this.createSimpleEntityHitboxProvider("skeleton", "standing", SKELETON_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.SLIME, this.createSlimeHitboxProvider());
        hitboxResolver.addEntityHitboxProvider(EntityType.SPIDER, this.createSimpleEntityHitboxProvider("spider", "standing", SPIDER_STANDING));
        hitboxResolver.addEntityHitboxProvider(EntityType.VILLAGER, this.createVillagerHitboxProvider());
        hitboxResolver.addEntityHitboxProvider(EntityType.WOLF, this.createWolfHitboxProvider());
        hitboxResolver.addEntityHitboxProvider(EntityType.ZOMBIE, this.createAgeableHitboxProvider("zombie", "adult-standing", "baby-standing", ZOMBIE_ADULT_STANDING, ZOMBIE_BABY_STANDING));

        return hitboxResolver;
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
        HitboxDefinitionResult hitboxDefinitionResult = hitboxConfiguration.getHitboxDefinition(entityType, pose);
        String errorMessage = hitboxDefinitionResult.getErrorMessage().orElse(null);
        HitboxDefinition hitboxDefinition = hitboxDefinitionResult.getHitboxDefinition().orElse(null);

        if (errorMessage != null) {
            logger.severe("Invalid hitbox for %s.%s: %s".formatted(entityType, pose, errorMessage));
            return defaultHitbox;
        }

        if (hitboxDefinition == null) {
            logger.severe("Missing hitbox for %s.%s; falling back to default".formatted(entityType, pose));
            return defaultHitbox;
        }

        return hitboxMapper.map(hitboxDefinition);
    }
}
