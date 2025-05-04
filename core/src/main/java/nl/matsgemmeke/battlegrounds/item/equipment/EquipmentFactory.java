package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EquipmentFactory {

    private static final String NAMESPACED_KEY_NAME = "battlegrounds-equipment";

    @NotNull
    private final DeploymentHandlerFactory deploymentHandlerFactory;
    @NotNull
    private final EquipmentControlsFactory controlsFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ItemEffectFactory effectFactory;
    @NotNull
    private final NamespacedKeyCreator keyCreator;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public EquipmentFactory(
            @NotNull DeploymentHandlerFactory deploymentHandlerFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull EquipmentControlsFactory controlsFactory,
            @NotNull ItemEffectFactory effectFactory,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull TaskRunner taskRunner
    ) {
        this.deploymentHandlerFactory = deploymentHandlerFactory;
        this.contextProvider = contextProvider;
        this.controlsFactory = controlsFactory;
        this.effectFactory = effectFactory;
        this.keyCreator = keyCreator;
        this.particleEffectMapper = particleEffectMapper;
        this.taskRunner = taskRunner;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        Equipment equipment = this.createInstance(spec, configuration, gameKey);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment);

        return equipment;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull ItemConfiguration configuration, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(spec, configuration, gameKey);
        equipment.setHolder(gamePlayer);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment, gamePlayer);

        return equipment;
    }

    @NotNull
    private Equipment createInstance(@NotNull EquipmentSpec spec, @NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        Section section = configuration.getRoot();

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setName(spec.name());
        equipment.setDescription(spec.description());

        // ItemStack creation
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = keyCreator.create(NAMESPACED_KEY_NAME);
        Material displayItemMaterial = Material.valueOf(spec.displayItem().material());
        int displayItemDamage = spec.displayItem().damage();
        String displayItemDisplayName = spec.displayItem().displayName();

        ItemTemplate displayItemTemplate = new ItemTemplate(uuid, key, displayItemMaterial);
        displayItemTemplate.setDamage(displayItemDamage);
        displayItemTemplate.setDisplayNameTemplate(new TextTemplate(displayItemDisplayName));

        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.update();

        ItemStackSpec activatorItemSpec = spec.activatorItem();
        ItemStackSpec throwItemSpec = spec.throwItem();

        if (activatorItemSpec != null) {
            UUID activatorUUID = UUID.randomUUID();
            NamespacedKey activatorKey = keyCreator.create(NAMESPACED_KEY_NAME);
            Material activatorItemMaterial = Material.valueOf(activatorItemSpec.material());

            ItemTemplate activatorItemTemplate = new ItemTemplate(activatorUUID, activatorKey, activatorItemMaterial);
            activatorItemTemplate.setDamage(activatorItemSpec.damage());
            activatorItemTemplate.setDisplayNameTemplate(new TextTemplate(activatorItemSpec.displayName()));

            DefaultActivator activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        if (throwItemSpec != null) {
            UUID throwItemUUID = UUID.randomUUID();
            NamespacedKey throwItemKey = keyCreator.create(NAMESPACED_KEY_NAME);
            Material throwItemMaterial = Material.valueOf(throwItemSpec.material());

            ItemTemplate throwItemTemplate = new ItemTemplate(throwItemUUID, throwItemKey, throwItemMaterial);
            throwItemTemplate.setDamage(throwItemSpec.damage());
            throwItemTemplate.setDisplayNameTemplate(new TextTemplate(throwItemSpec.displayName()));

            equipment.setThrowItemTemplate(throwItemTemplate);
        }

        ItemControls<EquipmentHolder> controls = controlsFactory.create(spec.controls(), spec.deployment(), equipment, gameKey);
        equipment.setControls(controls);

        DeploymentHandler deploymentHandler = this.setUpDeploymentHandler(spec.deployment(), spec.effect(), gameKey, equipment.getActivator());
        equipment.setDeploymentHandler(deploymentHandler);

        // Setting the projectile properties
        Section projectileSection = section.getSection("projectile");

        if (projectileSection != null) {
            ProjectileProperties projectileProperties = new ProjectileProperties();

            Section bounceSection = projectileSection.getSection("effects.bounce");
            Section soundSection = projectileSection.getSection("effects.sound");
            Section stickSection = projectileSection.getSection("effects.stick");
            Section trailSection = projectileSection.getSection("effects.trail");

            if (bounceSection != null) {
                int amountOfBounces = bounceSection.getInt("amount-of-bounces");
                double horizontalFriction = bounceSection.getDouble("horizontal-friction");
                double verticalFriction = bounceSection.getDouble("vertical-friction");
                long checkDelay = bounceSection.getLong("check-delay");
                long checkPeriod = bounceSection.getLong("check-period");

                BounceProperties properties = new BounceProperties(amountOfBounces, horizontalFriction, verticalFriction, checkDelay, checkPeriod);
                BounceEffect effect = new BounceEffect(taskRunner, properties);

                projectileProperties.getEffects().add(effect);
            }

            if (soundSection != null) {
                List<GameSound> sounds = DefaultGameSound.parseSounds(soundSection.getString("sounds"));
                List<Integer> intervals = soundSection.getIntList("intervals");

                SoundProperties properties = new SoundProperties(sounds, intervals);
                SoundEffect effect = new SoundEffect(audioEmitter, taskRunner, properties);

                projectileProperties.getEffects().add(effect);
            }

            if (stickSection != null) {
                List<GameSound> stickSounds = DefaultGameSound.parseSounds(stickSection.getString("stick-sounds"));
                long checkDelay = stickSection.getLong("check-delay");
                long checkPeriod = stickSection.getLong("check-period");

                StickProperties properties = new StickProperties(stickSounds, checkDelay, checkPeriod);
                StickEffect effect = new StickEffect(audioEmitter, taskRunner, properties);

                projectileProperties.getEffects().add(effect);
            }

            if (trailSection != null) {
                String particleValue = trailSection.getString("particle.type");
                Particle particle;

                try {
                    particle = Particle.valueOf(particleValue);
                } catch (IllegalArgumentException e) {
                    throw new EquipmentCreationException("Unable to create equipment item " + spec.name() + "; trail effect particle " + particleValue + " is invalid");
                }

                int count = trailSection.getInt("particle.count");
                double offsetX = trailSection.getDouble("particle.offset-x");
                double offsetY = trailSection.getDouble("particle.offset-y");
                double offsetZ = trailSection.getDouble("particle.offset-z");
                double extra = trailSection.getDouble("particle.extra");
                ParticleEffectProperties particleEffect = new ParticleEffectProperties(particle, count, offsetX, offsetY, offsetZ, extra);

                long checkDelay = trailSection.getLong("check-delay");
                long checkPeriod = trailSection.getLong("check-period");

                TrailProperties properties = new TrailProperties(particleEffect, checkDelay, checkPeriod);
                TrailEffect effect = new TrailEffect(taskRunner, properties);

                projectileProperties.getEffects().add(effect);
            }

            equipment.setProjectileProperties(projectileProperties);
        }

        return equipment;
    }

    @NotNull
    private DeploymentHandler setUpDeploymentHandler(@NotNull DeploymentSpec deploymentSpec, @NotNull ItemEffectSpec effectSpec, @NotNull GameKey gameKey, @Nullable Activator activator) {
        boolean activateEffectOnDestroy = deploymentSpec.activateEffectOnDestroy();
        boolean removeOnDestroy = deploymentSpec.removeOnDestroy();
        boolean resetEffectOnDestroy = deploymentSpec.resetEffectOnDestroy();

        List<GameSound> activationSounds = Collections.emptyList();
        long activationDelay = 0L;

        if (deploymentSpec.manualActivation() != null) {
            activationSounds = DefaultGameSound.parseSounds(deploymentSpec.manualActivation().activationSounds());
            activationDelay = deploymentSpec.manualActivation().activationDelay();
        }

        ParticleEffect destroyEffect = null;

        if (deploymentSpec.destroyEffect() != null) {
            destroyEffect = particleEffectMapper.map(deploymentSpec.destroyEffect());
        }

        DeploymentProperties deploymentProperties = new DeploymentProperties(activationSounds, destroyEffect, activateEffectOnDestroy, removeOnDestroy, resetEffectOnDestroy, activationDelay);

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        ItemEffect effect = effectFactory.create(effectSpec, gameKey);

        return deploymentHandlerFactory.create(deploymentProperties, audioEmitter, effect);
    }
}
