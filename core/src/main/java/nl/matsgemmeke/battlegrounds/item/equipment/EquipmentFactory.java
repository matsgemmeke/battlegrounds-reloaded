package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.*;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EquipmentFactory implements WeaponFactory {

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
    private final ItemEffectActivationFactory effectActivationFactory;
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
            @NotNull ItemEffectActivationFactory effectActivationFactory,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull TaskRunner taskRunner
    ) {
        this.deploymentHandlerFactory = deploymentHandlerFactory;
        this.contextProvider = contextProvider;
        this.controlsFactory = controlsFactory;
        this.effectFactory = effectFactory;
        this.effectActivationFactory = effectActivationFactory;
        this.keyCreator = keyCreator;
        this.particleEffectMapper = particleEffectMapper;
        this.taskRunner = taskRunner;
    }

    @NotNull
    public Equipment create(@NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        Equipment equipment = this.createInstance(configuration, gameKey);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment);

        return equipment;
    }

    @NotNull
    public Equipment create(@NotNull ItemConfiguration configuration, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(configuration, gameKey);
        equipment.setHolder(gamePlayer);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment, gamePlayer);

        return equipment;
    }

    @NotNull
    private Equipment createInstance(@NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        Section section = configuration.getRoot();

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

        String name = section.getString("name");
        String description = section.getString("description");

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setName(name);
        equipment.setDescription(description);

        // ItemStack creation
        Material material;
        String materialValue = section.getString("item.material");

        try {
            material = Material.valueOf(materialValue);
        } catch (IllegalArgumentException e) {
            throw new EquipmentCreationException("Unable to create equipment item " + name + "; item stack material " + materialValue + " is invalid");
        }

        UUID uuid = UUID.randomUUID();
        NamespacedKey key = keyCreator.create(NAMESPACED_KEY_NAME);
        int damage = section.getInt("item.damage");
        String displayName = section.getString("item.display-name");

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.setDamage(damage);

        if (displayName != null) {
            itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));
        }

        equipment.setItemTemplate(itemTemplate);
        equipment.update();

        // Setting the optional activator item
        Section activatorItemSection = section.getSection("item.activator");

        if (activatorItemSection != null) {
            Material activatorMaterial;
            String activatorMaterialValue = activatorItemSection.getString("material");

            try {
                activatorMaterial = Material.valueOf(activatorMaterialValue);
            } catch (IllegalArgumentException e) {
                throw new EquipmentCreationException("Unable to create equipment item " + name + "; activator item stack material " + activatorMaterialValue + " is invalid");
            }

            UUID activatorUUID = UUID.randomUUID();
            NamespacedKey activatorKey = keyCreator.create(NAMESPACED_KEY_NAME);
            int activatorDamage = activatorItemSection.getInt("damage");
            String activatorDisplayName = activatorItemSection.getString("display-name");

            ItemTemplate activatorItemTemplate = new ItemTemplate(activatorUUID, activatorKey, activatorMaterial);
            activatorItemTemplate.setDamage(activatorDamage);

            if (activatorDisplayName != null) {
                activatorItemTemplate.setDisplayNameTemplate(new TextTemplate(activatorDisplayName));
            }

            DefaultActivator activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        this.setUpDeploymentHandler(equipment, gameKey, section);

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
                List<GameSound> sounds = DefaultGameSound.parseSounds(soundSection.getString("sound"));
                List<Integer> intervals = soundSection.getIntList("intervals");

                SoundProperties properties = new SoundProperties(sounds, intervals);
                SoundEffect effect = new SoundEffect(audioEmitter, taskRunner, properties);

                projectileProperties.getEffects().add(effect);
            }

            if (stickSection != null) {
                List<GameSound> stickSounds = DefaultGameSound.parseSounds(stickSection.getString("stick-sound"));
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
                    throw new EquipmentCreationException("Unable to create equipment item " + name + "; trail effect particle " + particleValue + " is invalid");
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

        // Setting the throw item template
        Section throwItemSection = section.getSection("item.throw-item");

        if (throwItemSection != null) {
            Material throwItemMaterial;
            String throwItemMaterialValue = throwItemSection.getString("material");

            try {
                throwItemMaterial = Material.valueOf(throwItemMaterialValue);
            } catch (IllegalArgumentException e) {
                throw new EquipmentCreationException("Unable to create equipment item " + name + ", throw item material " + throwItemMaterialValue + " is invalid");
            }

            UUID throwItemUUID = UUID.randomUUID();
            NamespacedKey throwItemKey = keyCreator.create(NAMESPACED_KEY_NAME);
            int throwItemDamage = throwItemSection.getInt("damage");

            ItemTemplate throwItemTemplate = new ItemTemplate(throwItemUUID, throwItemKey, throwItemMaterial);
            throwItemTemplate.setDamage(throwItemDamage);

            equipment.setThrowItemTemplate(throwItemTemplate);
        }

        // Read controls configuration
        Section controlsSection = section.getSection("controls");

        if (controlsSection != null) {
            ItemControls<EquipmentHolder> controls = controlsFactory.create(section, equipment, gameKey);
            equipment.setControls(controls);
        }

        return equipment;
    }

    private void setUpDeploymentHandler(@NotNull DefaultEquipment equipment, @NotNull GameKey gameKey, @NotNull Section section) {
        Section deploySection = section.getOptionalSection("deploy")
                .orElseThrow(() -> new EquipmentCreationException("Unable to create equipment item " + equipment.getName() + ", deployment configuration is missing"));
        Section effectSection = section.getOptionalSection("effect")
                .orElseThrow(() -> new EquipmentCreationException("Unable to create equipment item " + equipment.getName() + ", effect configuration is missing"));
        Section effectActivationSection = section.getOptionalSection("effect.activation")
                .orElseThrow(() -> new EquipmentCreationException("Unable to create equipment item " + equipment.getName() + ", effect activation configuration is missing"));

        List<GameSound> activationSounds = DefaultGameSound.parseSounds(deploySection.getString("manual-activation.activation-sound"));
        boolean activateEffectOnDestroy = deploySection.getBoolean("on-destroy.activate");
        boolean removeOnDestroy = deploySection.getBoolean("on-destroy.remove");
        boolean resetEffectOnDestroy = deploySection.getBoolean("on-destroy.reset");
        long activationDelay = deploySection.getLong("manual-activation.activation-delay");

        ParticleEffect destroyParticleEffect = null;

        if (deploySection.contains("on-destroy.particle-effect")) {
            Map<String, Object> particleEffectValues = deploySection.getSection("on-destroy.particle-effect").getStringRouteMappedValues(true);

            destroyParticleEffect = particleEffectMapper.map(particleEffectValues);
        }

        DeploymentProperties deploymentProperties = new DeploymentProperties(activationSounds, destroyParticleEffect, activateEffectOnDestroy, removeOnDestroy, resetEffectOnDestroy, activationDelay);

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        Activator activator = equipment.getActivator();
        ItemEffectActivation effectActivation = effectActivationFactory.create(gameKey, effectActivationSection, activator);
        ItemEffect effect = effectFactory.create(effectSection, gameKey, effectActivation);

        DeploymentHandler deploymentHandler = deploymentHandlerFactory.create(deploymentProperties, audioEmitter, effect);

        equipment.setDeploymentHandler(deploymentHandler);
    }
}
