package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.*;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
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
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.api.SoundService;

import java.util.Collection;

public class ProjectileEffectFactory {

    private final ParticleEffectMapper particleEffectMapper;
    private final SoundEffectFactory soundEffectFactory;
    private final SoundService soundService;
    private final StickEffectFactory stickEffectFactory;
    private final TrailEffectFactory trailEffectFactory;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public ProjectileEffectFactory(
            ParticleEffectMapper particleEffectMapper,
            SoundEffectFactory soundEffectFactory,
            SoundService soundService,
            StickEffectFactory stickEffectFactory,
            TrailEffectFactory trailEffectFactory,
            TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.particleEffectMapper = particleEffectMapper;
        this.soundEffectFactory = soundEffectFactory;
        this.soundService = soundService;
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
                SoundSequence soundSequence = this.createSoundSequence(spec.soundSequence);
                ProjectileEffect soundEffect = soundEffectFactory.create(soundSequence);

                this.addTriggers(soundEffect, spec.triggers.values());

                yield soundEffect;
            }
            case STICK -> {
                StickEffectSpec spec = (StickEffectSpec) projectileEffectSpec;
                SoundSequence soundSequence = this.createSoundSequence(spec.soundSequence);
                ProjectileEffect stickEffect = stickEffectFactory.create(soundSequence);

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

    private SoundSequence createSoundSequence(String soundSequenceId) {
        return soundService.getSoundSequence(soundSequenceId)
                .orElseThrow(() -> new ProjectileEffectCreationException("Sound sequence '%s' does not exist".formatted(soundSequenceId)));
    }

    private void addTriggers(ProjectileEffect projectileEffect, Collection<TriggerSpec> triggerSpecs) {
        for (TriggerSpec triggerSpec : triggerSpecs) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

            projectileEffect.addTriggerExecutor(triggerExecutor);
        }
    }
}
