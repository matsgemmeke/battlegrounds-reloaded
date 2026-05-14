package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.*;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;

import java.util.Collection;
import java.util.List;

public class ProjectileEffectFactory {

    private final ParticleEffectMapper particleEffectMapper;
    private final SoundEffectFactory soundEffectFactory;
    private final StickEffectFactory stickEffectFactory;
    private final TrailEffectFactory trailEffectFactory;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public ProjectileEffectFactory(
            ParticleEffectMapper particleEffectMapper,
            SoundEffectFactory soundEffectFactory,
            StickEffectFactory stickEffectFactory,
            TrailEffectFactory trailEffectFactory,
            TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.particleEffectMapper = particleEffectMapper;
        this.soundEffectFactory = soundEffectFactory;
        this.stickEffectFactory = stickEffectFactory;
        this.trailEffectFactory = trailEffectFactory;
        this.triggerExecutorFactory = triggerExecutorFactory;
    }

    public ProjectileEffect create(ProjectileEffectSpec projectileEffectSpec) {
        ProjectileEffectType type = ProjectileEffectType.valueOf(projectileEffectSpec.type);

        return switch (type) {
            case BOUNCE -> {
                BounceEffectSpec spec = (BounceEffectSpec) projectileEffectSpec;
                BounceProperties properties = new BounceProperties(spec.maxActivations, spec.horizontalFriction, spec.verticalFriction);

                BounceEffect bounceEffect = new BounceEffect(properties);
                this.addTriggers(bounceEffect, spec.triggers.values());

                yield bounceEffect;
            }
            case SOUND -> {
                SoundEffectSpec spec = (SoundEffectSpec) projectileEffectSpec;
                List<GameSound> sounds = DefaultGameSound.parseSounds(spec.sounds);

                ProjectileEffect soundEffect = soundEffectFactory.create(sounds);
                this.addTriggers(soundEffect, spec.triggers.values());

                yield soundEffect;
            }
            case STICK -> {
                StickEffectSpec spec = (StickEffectSpec) projectileEffectSpec;
                List<GameSound> stickSounds = DefaultGameSound.parseSounds(spec.sounds);

                ProjectileEffect stickEffect = stickEffectFactory.create(stickSounds);
                this.addTriggers(stickEffect, spec.triggers.values());

                yield stickEffect;
            }
            case TRAIL -> {
                TrailEffectSpec spec = (TrailEffectSpec) projectileEffectSpec;
                ParticleEffect particleEffect = particleEffectMapper.map(spec.particleEffect);
                TrailProperties properties = new TrailProperties(particleEffect, spec.maxActivations);

                ProjectileEffect trailEffect = trailEffectFactory.create(properties);
                this.addTriggers(trailEffect, spec.triggers.values());

                yield trailEffect;
            }
        };
    }

    private void addTriggers(ProjectileEffect projectileEffect, Collection<TriggerSpec> triggerSpecs) {
        for (TriggerSpec triggerSpec : triggerSpecs) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

            projectileEffect.addTriggerExecutor(triggerExecutor);
        }
    }
}
