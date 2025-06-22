package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the immutable, validated configuration for a projectile effect.
 *
 * @param type               the particle effect type
 * @param sounds             the sounds used during the execution of the effect
 * @param horizontalFriction the amount of horizontal friction, will only be non-null when type equals to
 *                           {@code BOUNCE}
 * @param verticalFriction   the amount of vertical friction, will only be non-null when type equals to {@code BOUNCE}
 * @param maxActivations     the maximum amount of times the projectile effect will activate, will only be non-null
 *                           when type equals to {@code BOUNCE} or {@code TRAIL}
 * @param particleEffect     the particle effect used during the effect, will only be non-null when type equals to
 *                           {@code TRAIL}
 * @param triggers           the list of triggers used to activate the effect, will only be non-null when type equals
 *                           to {@code BOUNCE} or {@code STICK}
 */
public record ProjectileEffectSpec(
        @NotNull String type,
        @Nullable String sounds,
        @Nullable Double horizontalFriction,
        @Nullable Double verticalFriction,
        @Nullable Integer maxActivations,
        @Nullable ParticleEffectSpec particleEffect,
        @Nullable List<TriggerSpec> triggers
) { }
