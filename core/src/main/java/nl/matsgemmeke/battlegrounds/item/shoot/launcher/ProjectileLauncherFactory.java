package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
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
                List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);
                double velocity = this.validateSpecVar(spec.velocity, "velocity", projectileLauncherType);

                ArrowProperties properties = new ArrowProperties(launchSounds, velocity);
                ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

                return arrowLauncherFactory.create(properties, itemEffect);
            }
            case FIREBALL -> {
                List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);
                ParticleEffect trajectoryParticleEffect = this.createParticleEffect(spec.trajectoryParticleEffect);
                double velocity = this.validateSpecVar(spec.velocity, "velocity", projectileLauncherType);

                FireballProperties properties = new FireballProperties(launchSounds, trajectoryParticleEffect, velocity);
                ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

                return fireballLauncherFactory.create(properties, itemEffect);
            }
            case HITSCAN -> {
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
            case ITEM -> {
                return this.createItemLauncher(spec);
            }
            default -> throw new ProjectileLauncherCreationException("Invalid projectile launcher type '%s'".formatted(projectileLauncherType));
        }
    }

    private ProjectileLauncher createItemLauncher(ProjectileSpec spec) {
        double velocity = this.validateSpecVar(spec.velocity, "velocity", "ITEM");
        ItemSpec itemSpec = this.validateSpecVar(spec.item, "material", "ITEM");
        List<GameSound> launchSounds = DefaultGameSound.parseSounds(spec.launchSounds);

        NamespacedKey templateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
        UUID templateId = UUID.randomUUID();
        Material material = Material.valueOf(itemSpec.material);

        ItemTemplate itemTemplate = new ItemTemplate(templateKey, templateId, material);
        itemTemplate.setDamage(itemSpec.damage);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, launchSounds, velocity);
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

    private <T> T validateSpecVar(T value, String valueName, Object projectileLauncherType) {
        if (value == null) {
            throw new ProjectileLauncherCreationException("Cannot create projectile launcher for type %s because of invalid spec: Required '%s' value is missing".formatted(projectileLauncherType, valueName));
        }

        return value;
    }

    private ParticleEffect createParticleEffect(ParticleEffectSpec spec) {
        ParticleEffect particleEffect = null;

        if (spec != null) {
            particleEffect = particleEffectMapper.map(spec);
        }

        return particleEffect;
    }
}
