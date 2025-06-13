package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DeploymentProperties(
        @NotNull List<GameSound> manualActivationSounds,
        @Nullable ParticleEffect destructionParticleEffect,
        boolean activateEffectOnDestruction,
        boolean removeDeploymentOnDestruction,
        boolean undoEffectOnDestruction,
        boolean removeDeploymentOnCleanup,
        long manualActivationDelay
) { }
