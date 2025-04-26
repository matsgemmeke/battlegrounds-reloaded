package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents the immutable, validated configuration for a deployment system from a YAML file.
 *
 * @param health the amount of health that produced deployment objects have
 * @param activateEffectOnDestroy whether the deployment activates its effect when it gets destroyed
 * @param removeOnDestroy whether the deployment removes its deployment object when it gets destroyed
 * @param resetEffectOnDestroy whether the deployment resets its effect when it gets destroyed
 * @param destroyEffect the optional particle effect produced when the deployment gets destroyed
 * @param resistances the resistances for certain damage types the deployment has
 * @param throwProperties the properties used for throw deployments, will be non-null when throw controls are enabled for the item
 * @param cookProperties the properties used for cook deployments, will be non-null when throw and cook controls are enabled for the item
 * @param placeProperties the properties used for cook deployments, will be non-null when place controls are enabled for the item
 * @param manualActivation the properties used for manual activations, will be non-null when activate controls are enabled for the item
 */
public record DeploymentSpec(
        @NotNull Double health,
        @NotNull Boolean activateEffectOnDestroy,
        @NotNull Boolean removeOnDestroy,
        @NotNull Boolean resetEffectOnDestroy,
        @Nullable ParticleEffectSpec destroyEffect,
        @NotNull Map<String, Double> resistances,
        @Nullable ThrowPropertiesSpec throwProperties,
        @Nullable CookPropertiesSpec cookProperties,
        @Nullable PlacePropertiesSpec placeProperties,
        @Nullable ManualActivationSpec manualActivation
) { }
