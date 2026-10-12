package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.*;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow.ArrowLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow.ArrowLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow.ArrowProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.item.ItemLaunchProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.item.ItemLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.item.ItemLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import nl.matsgemmeke.battlegrounds.sound.api.SoundService;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ProjectileLauncherFactory {

    private final ArrowLauncherFactory arrowLauncherFactory;
    private final FireballLauncherFactory fireballLauncherFactory;
    private final HitscanLauncherFactory hitscanLauncherFactory;
    private final ItemEffectFactory itemEffectFactory;
    private final ItemLauncherFactory itemLauncherFactory;
    private final ItemTemplateFactory itemTemplateFactory;
    private final ParticleEffectMapper particleEffectMapper;
    private final SoundService soundService;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public ProjectileLauncherFactory(
            ArrowLauncherFactory arrowLauncherFactory,
            FireballLauncherFactory fireballLauncherFactory,
            HitscanLauncherFactory hitscanLauncherFactory,
            ItemEffectFactory itemEffectFactory,
            ItemLauncherFactory itemLauncherFactory,
            ItemTemplateFactory itemTemplateFactory,
            ParticleEffectMapper particleEffectMapper,
            SoundService soundService,
            TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.arrowLauncherFactory = arrowLauncherFactory;
        this.fireballLauncherFactory = fireballLauncherFactory;
        this.hitscanLauncherFactory = hitscanLauncherFactory;
        this.itemEffectFactory = itemEffectFactory;
        this.itemLauncherFactory = itemLauncherFactory;
        this.itemTemplateFactory = itemTemplateFactory;
        this.particleEffectMapper = particleEffectMapper;
        this.soundService = soundService;
        this.triggerExecutorFactory = triggerExecutorFactory;
    }

    public ProjectileLauncher create(ProjectileSpec spec) {
        ProjectileLauncherType projectileLauncherType = ProjectileLauncherType.valueOf(spec.type);

        switch (projectileLauncherType) {
            case ARROW -> {
                return this.createArrowLauncher((ArrowProjectileSpec) spec);
            }
            case FIREBALL -> {
                return this.createFireballLauncher((FireballProjectileSpec) spec);
            }
            case HITSCAN -> {
                return this.createHitscanLauncher((HitscanProjectileSpec) spec);
            }
            case ITEM -> {
                return this.createItemLauncher((ItemProjectileSpec) spec);
            }
            default -> throw new ProjectileLauncherCreationException("Invalid projectile launcher type '%s'".formatted(projectileLauncherType));
        }
    }

    private ProjectileLauncher createArrowLauncher(ArrowProjectileSpec spec) {
        SoundTemplate launchSoundTemplate = this.createLaunchSoundTemplate(spec);
        double velocity = spec.velocity;

        ArrowProperties properties = new ArrowProperties(launchSoundTemplate, velocity);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        ArrowLauncher arrowLauncher = arrowLauncherFactory.create(properties, itemEffect);

        for (TriggerSpec triggerSpec : spec.triggers.values()) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

            arrowLauncher.addTriggerExecutor(triggerExecutor);
        }

        return arrowLauncher;
    }

    private ProjectileLauncher createFireballLauncher(FireballProjectileSpec spec) {
        SoundTemplate launchSoundTemplate = this.createLaunchSoundTemplate(spec);
        ParticleEffect trajectoryParticleEffect = this.createParticleEffect(spec.trajectoryParticleEffect);
        double velocity = spec.velocity;

        FireballProperties properties = new FireballProperties(launchSoundTemplate, trajectoryParticleEffect, velocity);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        return fireballLauncherFactory.create(properties, itemEffect);
    }

    private ProjectileLauncher createHitscanLauncher(HitscanProjectileSpec spec) {
        SoundTemplate launchSoundTemplate = this.createLaunchSoundTemplate(spec);

        ParticleEffect trajectoryParticleEffect = null;
        ParticleEffectSpec trajectoryParticleEffectSpec = spec.trajectoryParticleEffect;

        if (trajectoryParticleEffectSpec != null) {
            trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
        }

        HitscanProperties properties = new HitscanProperties(launchSoundTemplate, trajectoryParticleEffect);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        return hitscanLauncherFactory.create(properties, itemEffect);
    }

    private ProjectileLauncher createItemLauncher(ItemProjectileSpec spec) {
        double velocity = spec.velocity;
        int pickupDelay = spec.pickupDelay;
        List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);
        ItemTemplate itemTemplate = itemTemplateFactory.create(spec.item);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, launchSounds, velocity, pickupDelay);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        ItemLauncher itemLauncher = itemLauncherFactory.create(properties, itemEffect);

        for (TriggerSpec triggerSpec : spec.triggers.values()) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

            itemLauncher.addTriggerExecutor(triggerExecutor);
        }

        return itemLauncher;
    }

    @Nullable
    private SoundTemplate createLaunchSoundTemplate(ProjectileSpec spec) {
        String launchSoundTemplateId = spec.launchSoundTemplate;

        return Optional.ofNullable(launchSoundTemplateId).flatMap(soundService::getSoundTemplate).orElse(null);
    }

    private ParticleEffect createParticleEffect(ParticleEffectSpec spec) {
        ParticleEffect particleEffect = null;

        if (spec != null) {
            particleEffect = particleEffectMapper.map(spec);
        }

        return particleEffect;
    }
}
