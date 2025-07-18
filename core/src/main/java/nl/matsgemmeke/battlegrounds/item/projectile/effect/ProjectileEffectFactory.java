package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ProjectileEffectFactory {

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;
    @NotNull
    private final TrailEffectFactory trailEffectFactory;
    @NotNull
    private final TriggerFactory triggerFactory;

    @Inject
    public ProjectileEffectFactory(
            @NotNull GameContextProvider contextProvider,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull TrailEffectFactory trailEffectFactory,
            @NotNull TriggerFactory triggerFactory
    ) {
        this.contextProvider = contextProvider;
        this.particleEffectMapper = particleEffectMapper;
        this.trailEffectFactory = trailEffectFactory;
        this.triggerFactory = triggerFactory;
    }

    @NotNull
    public ProjectileEffect create(@NotNull ProjectileEffectSpec spec, @NotNull GameKey gameKey) {
        ProjectileEffectType type = ProjectileEffectType.valueOf(spec.type);

        switch (type) {
            case BOUNCE -> {
                int amountOfBounces = this.validateSpecVar(spec.maxActivations, "maxActivations", type);
                double horizontalFriction = this.validateSpecVar(spec.horizontalFriction, "horizontalFriction", type);
                double verticalFriction = this.validateSpecVar(spec.verticalFriction, "verticalFriction", type);
                Map<String, TriggerSpec> triggerSpecs = this.validateSpecVar(spec.triggers, "triggers", type);

                BounceProperties properties = new BounceProperties(amountOfBounces, horizontalFriction, verticalFriction);

                BounceEffect bounceEffect = new BounceEffect(properties);
                this.addTriggers(bounceEffect, gameKey, triggerSpecs.values());

                return bounceEffect;
            }
            case SOUND -> {
                List<GameSound> sounds = DefaultGameSound.parseSounds(spec.sounds);
                Map<String, TriggerSpec> triggerSpecs = this.validateSpecVar(spec.triggers, "triggers", type);

                AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

                SoundEffect soundEffect = new SoundEffect(audioEmitter, sounds);
                this.addTriggers(soundEffect, gameKey, triggerSpecs.values());

                return soundEffect;

            }
            case STICK -> {
                List<GameSound> stickSounds = DefaultGameSound.parseSounds(spec.sounds);
                Map<String, TriggerSpec> triggerSpecs = this.validateSpecVar(spec.triggers, "triggers", type);

                AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

                StickEffect stickEffect = new StickEffect(audioEmitter, stickSounds);
                this.addTriggers(stickEffect, gameKey, triggerSpecs.values());

                return stickEffect;
            }
            case TRAIL -> {
                ParticleEffectSpec particleEffectSpec = this.validateSpecVar(spec.particleEffect, "particleEffect", type);
                Integer maxActivations = this.validateSpecVar(spec.maxActivations, "maxActivations", type);
                Map<String, TriggerSpec> triggerSpecs = this.validateSpecVar(spec.triggers, "triggers", type);

                ParticleEffect particleEffect = particleEffectMapper.map(particleEffectSpec);
                TrailProperties properties = new TrailProperties(particleEffect, maxActivations);

                ProjectileEffect trailEffect = trailEffectFactory.create(properties);
                this.addTriggers(trailEffect, gameKey, triggerSpecs.values());

                return trailEffect;
            }
            default -> throw new ProjectileEffectCreationException("Unknown projectile effect type '%s'".formatted(spec.type));
        }
    }

    /**
     * Acts as a double check to validate the presence of nullable specification variables.
     *
     * @param value                the specification value
     * @param valueName            the name of the value, to create error messages
     * @param projectileEffectType the name of the projectile effect type, to create error messages
     * @return the given value
     * @throws ProjectileEffectCreationException if the value is null
     * @param <T> the value type
     */
    private <T> T validateSpecVar(@Nullable T value, @NotNull String valueName, @NotNull Object projectileEffectType) {
        if (value == null) {
            throw new ProjectileEffectCreationException("Cannot create %s because of invalid spec: Required '%s' value is missing".formatted(projectileEffectType, valueName));
        }

        return value;
    }

    private void addTriggers(@NotNull ProjectileEffect projectileEffect, @NotNull GameKey gameKey, @NotNull Collection<TriggerSpec> triggerSpecs) {
        for (TriggerSpec triggerSpec : triggerSpecs) {
            Trigger trigger = triggerFactory.create(triggerSpec, gameKey);
            projectileEffect.addTrigger(trigger);
        }
    }
}
