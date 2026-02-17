package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.effect.*;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PolymorphicTypeRegistry {

    private static final Map<Class<?>, PolymorphicDefinition> definitions = new HashMap<>();

    static {
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
    }

    public static void register(Class<?> baseClass, String discriminator, Map<String, Class<?>> mappings) {
        definitions.put(baseClass, new PolymorphicDefinition(discriminator, mappings));
    }

    public static Optional<PolymorphicDefinition> get(Class<?> baseType) {
        return Optional.ofNullable(definitions.get(baseType));
    }
}
