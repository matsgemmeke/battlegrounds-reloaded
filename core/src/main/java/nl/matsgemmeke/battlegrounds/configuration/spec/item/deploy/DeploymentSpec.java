package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record DeploymentSpec(
        @NotNull Double health,
        @NotNull Boolean activateEffectOnDestroy,
        @NotNull Boolean removeOnDestroy,
        @NotNull Boolean resetEffectOnDestroy,
        @Nullable ParticleEffectSpec destroyEffect,
        @NotNull Map<String, Double> resistances,
        @Nullable ThrowPropertiesSpec throwPropertiesSpec,
        @Nullable CookPropertiesSpec cookPropertiesSpec,
        @Nullable PlacePropertiesSpec placePropertiesSpec,
        @Nullable ManualActivationSpec manualActivationSpec
) { }
