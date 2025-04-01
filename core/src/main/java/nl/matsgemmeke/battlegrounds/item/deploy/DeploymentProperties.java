package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DeploymentProperties(
        @NotNull List<GameSound> activationSounds,
        @Nullable ParticleEffect destroyParticleEffect,
        boolean activateEffectOnDestroy,
        boolean removeOnDestroy,
        boolean resetEffectOnDestroy,
        long activationDelay
) { }
