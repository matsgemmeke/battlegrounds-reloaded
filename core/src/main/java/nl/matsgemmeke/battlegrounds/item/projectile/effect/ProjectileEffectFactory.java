package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectileEffectFactory {

    @NotNull
    private final BounceEffectFactory bounceEffectFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;
    @NotNull
    private final SoundEffectFactory soundEffectFactory;
    @NotNull
    private final StickEffectFactory stickEffectFactory;
    @NotNull
    private final TrailEffectFactory trailEffectFactory;
    @NotNull
    private final TriggerFactory triggerFactory;

    @Inject
    public ProjectileEffectFactory(
            @NotNull BounceEffectFactory bounceEffectFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull SoundEffectFactory soundEffectFactory,
            @NotNull StickEffectFactory stickEffectFactory,
            @NotNull TrailEffectFactory trailEffectFactory,
            @NotNull TriggerFactory triggerFactory
    ) {
        this.bounceEffectFactory = bounceEffectFactory;
        this.contextProvider = contextProvider;
        this.particleEffectMapper = particleEffectMapper;
        this.soundEffectFactory = soundEffectFactory;
        this.stickEffectFactory = stickEffectFactory;
        this.trailEffectFactory = trailEffectFactory;
        this.triggerFactory = triggerFactory;
    }

    @NotNull
    public ProjectileEffect create(@NotNull ProjectileEffectSpec spec, @NotNull GameKey gameKey) {
        ProjectileEffectType type = ProjectileEffectType.valueOf(spec.type());

        switch (type) {
            case BOUNCE -> {
                int amountOfBounces = this.validateSpecVar(spec.maxActivations(), "maxActivations", type);
                double horizontalFriction = this.validateSpecVar(spec.horizontalFriction(), "horizontalFriction", type);
                double verticalFriction = this.validateSpecVar(spec.verticalFriction(), "verticalFriction", type);
                List<TriggerSpec> triggerSpecs = this.validateSpecVar(spec.triggers(), "triggers", type);

                Set<Trigger> triggers = triggerSpecs.stream()
                        .map(triggerSpec -> triggerFactory.create(triggerSpec, gameKey))
                        .collect(Collectors.toSet());

                BounceProperties properties = new BounceProperties(amountOfBounces, horizontalFriction, verticalFriction, 1L, 1L);

                return bounceEffectFactory.create(properties, triggers);
            }
            case SOUND -> {
                List<GameSound> sounds = DefaultGameSound.parseSounds(spec.sounds());
                Long delay = this.validateSpecVar(spec.delay(), "delay", type);
                List<Long> intervals = this.validateSpecVar(spec.intervals(), "intervals", type);

                SoundProperties properties = new SoundProperties(sounds, delay, intervals);
                AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

                return soundEffectFactory.create(properties, audioEmitter);

            }
            case STICK -> {
                List<GameSound> stickSounds = DefaultGameSound.parseSounds(spec.sounds());
                Long delay = this.validateSpecVar(spec.delay(), "delay", type);

                StickProperties properties = new StickProperties(stickSounds, delay);
                AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

                return stickEffectFactory.create(properties, audioEmitter);
            }
            case TRAIL -> {
                ParticleEffectSpec particleEffectSpec = this.validateSpecVar(spec.particleEffect(), "particleEffect", type);
                Long delay = this.validateSpecVar(spec.delay(), "delay", type);
                List<Long> intervals = this.validateSpecVar(spec.intervals(), "intervals", type);
                Integer maxActivations = this.validateSpecVar(spec.maxActivations(), "maxActivations", type);

                ParticleEffect particleEffect = particleEffectMapper.map(particleEffectSpec);
                TrailProperties properties = new TrailProperties(particleEffect, delay, intervals, maxActivations);

                return trailEffectFactory.create(properties);
            }
            default -> throw new ProjectileEffectCreationException("Unknown projectile effect type '%s'".formatted(spec.type()));
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
}
