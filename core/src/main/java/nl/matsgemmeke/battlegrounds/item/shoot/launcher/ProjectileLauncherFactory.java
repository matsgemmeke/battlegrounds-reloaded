package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.*;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
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
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.UUID;

public class ProjectileLauncherFactory {

    private static final String TEMPLATE_ID_KEY = "template-id";

    private final ArrowLauncherFactory arrowLauncherFactory;
    private final FireballLauncherFactory fireballLauncherFactory;
    private final HitscanLauncherFactory hitscanLauncherFactory;
    private final ItemEffectFactory itemEffectFactory;
    private final ItemLauncherFactory itemLauncherFactory;
    private final NamespacedKeyCreator namespacedKeyCreator;
    private final ParticleEffectMapper particleEffectMapper;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public ProjectileLauncherFactory(
            ArrowLauncherFactory arrowLauncherFactory,
            FireballLauncherFactory fireballLauncherFactory,
            HitscanLauncherFactory hitscanLauncherFactory,
            ItemEffectFactory itemEffectFactory,
            ItemLauncherFactory itemLauncherFactory,
            NamespacedKeyCreator namespacedKeyCreator,
            ParticleEffectMapper particleEffectMapper,
            TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.arrowLauncherFactory = arrowLauncherFactory;
        this.fireballLauncherFactory = fireballLauncherFactory;
        this.hitscanLauncherFactory = hitscanLauncherFactory;
        this.itemEffectFactory = itemEffectFactory;
        this.itemLauncherFactory = itemLauncherFactory;
        this.namespacedKeyCreator = namespacedKeyCreator;
        this.particleEffectMapper = particleEffectMapper;
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
        List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);
        double velocity = spec.velocity;

        ArrowProperties properties = new ArrowProperties(launchSounds, velocity);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        ArrowLauncher arrowLauncher = arrowLauncherFactory.create(properties, itemEffect);

        if (spec.triggers != null) {
            for (TriggerSpec triggerSpec : spec.triggers.values()) {
                TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

                arrowLauncher.addTriggerExecutor(triggerExecutor);
            }
        }

        return arrowLauncher;
    }

    private ProjectileLauncher createFireballLauncher(FireballProjectileSpec spec) {
        List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);
        ParticleEffect trajectoryParticleEffect = this.createParticleEffect(spec.trajectoryParticleEffect);
        double velocity = spec.velocity;

        FireballProperties properties = new FireballProperties(launchSounds, trajectoryParticleEffect, velocity);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        return fireballLauncherFactory.create(properties, itemEffect);
    }

    private ProjectileLauncher createHitscanLauncher(HitscanProjectileSpec spec) {
        List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);
        ParticleEffect trajectoryParticleEffect = null;
        ParticleEffectSpec trajectoryParticleEffectSpec = spec.trajectoryParticleEffect;

        if (trajectoryParticleEffectSpec != null) {
            trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
        }

        HitscanProperties properties = new HitscanProperties(launchSounds, trajectoryParticleEffect);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        return hitscanLauncherFactory.create(properties, itemEffect);
    }

    private ProjectileLauncher createItemLauncher(ItemProjectileSpec spec) {
        double velocity = spec.velocity;
        int pickupDelay = spec.pickupDelay;
        List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);

        NamespacedKey templateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
        UUID templateId = UUID.randomUUID();
        Material material = Material.valueOf(spec.item.material);

        ItemTemplate itemTemplate = new ItemTemplate(templateKey, templateId, material);
        itemTemplate.setDamage(spec.item.damage);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, launchSounds, velocity, pickupDelay);
        ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

        ItemLauncher itemLauncher = itemLauncherFactory.create(properties, itemEffect);

        if (spec.triggers != null) {
            for (TriggerSpec triggerSpec : spec.triggers.values()) {
                TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

                itemLauncher.addTriggerExecutor(triggerExecutor);
            }
        }

        return itemLauncher;
    }

    private ParticleEffect createParticleEffect(ParticleEffectSpec spec) {
        ParticleEffect particleEffect = null;

        if (spec != null) {
            particleEffect = particleEffectMapper.map(spec);
        }

        return particleEffect;
    }
}
