package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.effect.*;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.*;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.*;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.BurstModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.FireModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.FullyAutomaticModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.SemiAutomaticSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread.BuckshotSpreadPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread.SingleProjectileSpreadPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread.SpreadPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PolymorphicTypeRegistry {

    private static final Map<Class<?>, PolymorphicDefinition> definitions = new HashMap<>();

    static {
        register(FireModeSpec.class, "type", Map.of(
                "BURST_MODE", BurstModeSpec.class,
                "FULLY_AUTOMATIC", FullyAutomaticModeSpec.class,
                "SEMI_AUTOMATIC", SemiAutomaticSpec.class
        ));

        register(ItemEffectSpec.class, "type", Map.of(
                "COMBUSTION", CombustionEffectSpec.class,
                "DAMAGE", DamageEffectSpec.class,
                "EXPLOSION", ExplosionEffectSpec.class,
                "FLASH", FlashEffectSpec.class,
                "GUN_FIRE_SIMULATION", GunFireSimulationEffectSpec.class,
                "MARK_SPAWN_POINT", MarkSpawnPointEffectSpec.class,
                "SMOKE_SCREEN", SmokeScreenEffectSpec.class,
                "SOUND_NOTIFICATION", SoundNotificationEffectSpec.class
        ));

        register(ProjectileSpec.class, "type", Map.of(
                "ARROW", ArrowProjectileSpec.class,
                "FIREBALL", FireballProjectileSpec.class,
                "HITSCAN", HitscanProjectileSpec.class,
                "ITEM", ItemProjectileSpec.class
        ));

        register(ProjectileEffectSpec.class, "type", Map.of(
                "BOUNCE", BounceEffectSpec.class,
                "SOUND", SoundEffectSpec.class,
                "STICK", StickEffectSpec.class,
                "TRAIL", TrailEffectSpec.class
        ));

        register(SpreadPatternSpec.class, "type", Map.of(
                "BUCKSHOT", BuckshotSpreadPatternSpec.class,
                "SINGLE_PROJECTILE", SingleProjectileSpreadPatternSpec.class
        ));

        register(TriggerSpec.class, "type", Map.of(
                "BLOCK_IMPACT", BlockImpactTriggerSpec.class,
                "ENEMY_PROXIMITY", EnemyProximityTriggerSpec.class,
                "ENTITY_IMPACT", EntityImpactTriggerSpec.class,
                "FLOOR_HIT", FloorHitTriggerSpec.class,
                "SCHEDULED", ScheduledTriggerSpec.class
        ));
    }

    public static void register(Class<?> baseClass, String discriminator, Map<String, Class<?>> mappings) {
        definitions.put(baseClass, new PolymorphicDefinition(discriminator, mappings));
    }

    public static Optional<PolymorphicDefinition> get(Class<?> baseType) {
        return Optional.ofNullable(definitions.get(baseType));
    }
}
