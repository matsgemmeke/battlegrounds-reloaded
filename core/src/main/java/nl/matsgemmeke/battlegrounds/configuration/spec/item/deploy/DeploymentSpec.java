package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents the immutable, validated configuration for a deployment system from a YAML file.
 *
 * @param health                        the amount of health that produced deployment objects have
 * @param activateEffectOnDestruction   whether the deployment activates its effect when it gets destroyed
 * @param removeDeploymentOnDestruction whether the deployment removes its deployment object when it gets destroyed
 * @param undoEffectOnDestruction       whether the deployment resets its effect when it gets destroyed
 * @param removeDeploymentOnCleanup     whether the deployment removes its deployment object when the item gets cleaned up
 * @param destructionParticleEffect     the optional particle effect produced when the deployment gets destroyed
 * @param resistances                   the resistances for certain damage types the deployment has
 * @param throwProperties               the properties used for throw deployments, will be non-null when throw controls are
 *                                      enabled for the item
 * @param cookProperties                the properties used for cook deployments, will be non-null when throw and cook
 *                                      controls are enabled for the item
 * @param placeProperties               the properties used for cook deployments, will be non-null when place controls are
 *                                      enabled for the item
 * @param manualActivation              the properties used for manual activations, will be non-null when activate controls
 *                                      are enabled for the item
 */
public record DeploymentSpec(
        @NotNull Double health,
        @NotNull Boolean activateEffectOnDestruction,
        @NotNull Boolean removeDeploymentOnDestruction,
        @NotNull Boolean undoEffectOnDestruction,
        @NotNull Boolean removeDeploymentOnCleanup,
        @Nullable ParticleEffectSpec destructionParticleEffect,
        @NotNull Map<String, Double> resistances,
        @Nullable ThrowPropertiesSpec throwProperties,
        @Nullable CookPropertiesSpec cookProperties,
        @Nullable PlacePropertiesSpec placeProperties,
        @Nullable ManualActivationSpec manualActivation
) { }
