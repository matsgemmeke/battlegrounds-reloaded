package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import org.jetbrains.annotations.Nullable;

public record DeploymentProperties(
        @Nullable
        SoundSequence manualActivationSoundSequence,
        @Nullable
        ParticleEffect destructionParticleEffect,
        boolean activateEffectOnDestruction,
        boolean removeDeploymentOnDestruction,
        boolean undoEffectOnDestruction,
        boolean removeDeploymentOnReset,
        long manualActivationDelay
) { }
