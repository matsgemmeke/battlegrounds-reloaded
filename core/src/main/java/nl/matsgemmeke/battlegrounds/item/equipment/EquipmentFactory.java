package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.MappingException;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.*;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.*;
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
import nl.matsgemmeke.battlegrounds.util.UUIDGenerator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentFactory implements WeaponFactory {

    private static final String NAMESPACED_KEY_NAME = "battlegrounds-equipment";
    private static final UUIDGenerator UUID_GENERATOR = new UUIDGenerator();

    @NotNull
    private ItemEffectFactory effectFactory;
    @NotNull
    private ItemEffectActivationFactory effectActivationFactory;
    @NotNull
    private NamespacedKeyCreator keyCreator;
    @NotNull
    private TaskRunner taskRunner;

    public EquipmentFactory(
            @NotNull ItemEffectFactory effectFactory,
            @NotNull ItemEffectActivationFactory effectActivationFactory,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull TaskRunner taskRunner
    ) {
        this.effectFactory = effectFactory;
        this.effectActivationFactory = effectActivationFactory;
        this.keyCreator = keyCreator;
        this.taskRunner = taskRunner;
    }

    @NotNull
    public Equipment make(@NotNull ItemConfiguration configuration, @NotNull GameContext context) {
        Equipment equipment = this.createInstance(configuration, context);

        context.getEquipmentRegistry().registerItem(equipment);

        return equipment;
    }

    @NotNull
    public Equipment make(@NotNull ItemConfiguration configuration, @NotNull GameContext context, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(configuration, context);
        equipment.setHolder(gamePlayer);

        context.getEquipmentRegistry().registerItem(equipment, gamePlayer);

        return equipment;
    }

    @NotNull
    private Equipment createInstance(@NotNull ItemConfiguration configuration, @NotNull GameContext context) {
        Section section = configuration.getRoot();

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
            throw new CreateEquipmentException("Unable to create equipment item " + name + "; item stack material " + materialValue + " is invalid");
        }

        NamespacedKey key = keyCreator.create(NAMESPACED_KEY_NAME);
        int damage = section.getInt("item.damage");
        String displayName = section.getString("item.display-name");

        ItemTemplate itemTemplate = new ItemTemplate(key, material, UUID_GENERATOR);
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
                throw new CreateEquipmentException("Unable to create equipment item " + name + "; activator item stack material " + activatorMaterialValue + " is invalid");
            }

            NamespacedKey activatorKey = keyCreator.create(NAMESPACED_KEY_NAME);
            int activatorDamage = activatorItemSection.getInt("damage");
            String activatorDisplayName = activatorItemSection.getString("display-name");

            ItemTemplate activatorItemTemplate = new ItemTemplate(activatorKey, activatorMaterial, UUID_GENERATOR);
            activatorItemTemplate.setDamage(activatorDamage);

            if (activatorDisplayName != null) {
                activatorItemTemplate.setDisplayNameTemplate(new TextTemplate(activatorDisplayName));
            }

            DefaultActivator activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        // Setting the effect activation
        Section effectSection = section.getSection("effect");
        Section effectActivationSection = section.getSection("effect.activation");

        if (effectSection != null && effectActivationSection != null) {
            Activator activator = equipment.getActivator();

            ItemEffectActivation effectActivation = effectActivationFactory.make(context, effectActivationSection, activator);
            ItemEffect effect = effectFactory.make(effectSection, context, effectActivation);

            equipment.setEffect(effect);
        }

        // Setting the deployment properties
        Section deploySection = section.getSection("deploy");

        if (deploySection != null) {
            boolean activateOnDestroy = deploySection.getBoolean("activate-on-destroy");
            boolean resetOnDestroy = deploySection.getBoolean("reset-on-destroy");
            double health = deploySection.getDouble("health");
            Map<DamageType, Double> resistances = new HashMap<>();

            if (deploySection.contains("resistances.bullet-damage")) {
                resistances.put(DamageType.BULLET_DAMAGE, deploySection.getDouble("resistances.bullet-damage"));
            }
            if (deploySection.contains("resistances.explosive-damage")) {
                resistances.put(DamageType.EXPLOSIVE_DAMAGE, deploySection.getDouble("resistances.explosive-damage"));
            }
            if (deploySection.contains("resistances.fire-damage")) {
                resistances.put(DamageType.FIRE_DAMAGE, deploySection.getDouble("resistances.fire-damage"));
            }

            DeploymentProperties deploymentProperties = new DeploymentProperties();
            deploymentProperties.setActivatedOnDestroy(activateOnDestroy);
            deploymentProperties.setHealth(health);
            deploymentProperties.setResetOnDestroy(resetOnDestroy);
            deploymentProperties.setResistances(resistances);

            if (deploySection.contains("on-destroy.particle-effect")) {
                Map<String, Object> particleEffectValues = deploySection.getSection("on-destroy.particle-effect").getStringRouteMappedValues(true);
                ParticleEffectMapper mapper = new ParticleEffectMapper();
                ParticleEffect particleEffect;

                try {
                    particleEffect = mapper.map(particleEffectValues);
                } catch (MappingException e) {
                    throw new CreateEquipmentException("Unable to create equipment item " + name + ": " + e.getMessage());
                }

                deploymentProperties.setDestroyParticleEffect(particleEffect);
            }

            equipment.setDeploymentProperties(deploymentProperties);
        }

        // Setting the projectile properties
        Section projectileSection = section.getSection("projectile");

        if (projectileSection != null) {
            ProjectileProperties projectileProperties = new ProjectileProperties();

            Section bounceSection = projectileSection.getSection("effects.bounce");
            Section soundSection = projectileSection.getSection("effects.sound");
            Section stickSection = projectileSection.getSection("effects.stick");
            Section trailSection = projectileSection.getSection("effects.trail");

            AudioEmitter audioEmitter = context.getAudioEmitter();

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
                    throw new CreateEquipmentException("Unable to create equipment item " + name + "; trail effect particle " + particleValue + " is invalid");
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
                throw new CreateEquipmentException("Unable to create equipment item " + name + ", throw item material " + throwItemMaterialValue + " is invalid");
            }

            NamespacedKey throwItemKey = keyCreator.create(NAMESPACED_KEY_NAME);
            int throwItemDamage = throwItemSection.getInt("damage");

            ItemTemplate throwItemTemplate = new ItemTemplate(throwItemKey, throwItemMaterial, UUID_GENERATOR);
            throwItemTemplate.setDamage(throwItemDamage);

            equipment.setThrowItemTemplate(throwItemTemplate);
        }

        // Read controls configuration
        Section controlsSection = section.getSection("controls");

        if (controlsSection != null) {
            this.addControls(equipment, context, section, controlsSection);
        }

        return equipment;
    }

    private void addControls(@NotNull DefaultEquipment equipment, @NotNull GameContext context, @NotNull Section section, @NotNull Section controlsSection) {
        AudioEmitter audioEmitter = context.getAudioEmitter();

        String activateActionValue = controlsSection.getString("activate");
        String cookActionValue = controlsSection.getString("cook");
        String placeActionValue = controlsSection.getString("place");
        String throwActionValue = controlsSection.getString("throw");

        if (throwActionValue != null) {
            Action throwAction = this.getActionFromConfiguration("throw", throwActionValue);

            if (cookActionValue != null) {
                Action cookAction = this.getActionFromConfiguration("cook", cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(section.getString("throwing.cook-sound"));

                CookProperties cookProperties = new CookProperties(cookSounds);
                CookFunction cookFunction = new CookFunction(cookProperties, equipment, audioEmitter);

                equipment.getControls().addControl(cookAction, cookFunction);
            }

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(section.getString("throwing.throw-sound"));
            double velocity = section.getDouble("throwing.velocity");
            long delayAfterThrow = section.getLong("throwing.delay-after-throw");

            ThrowProperties properties = new ThrowProperties(throwSounds, velocity, delayAfterThrow);
            ThrowFunction throwFunction = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);

            equipment.getControls().addControl(throwAction, throwFunction);
        }

        if (placeActionValue != null) {
            Action placeAction = this.getActionFromConfiguration("place", placeActionValue);

            Material material;
            String materialValue = section.getString("placing.material");

            try {
                material = Material.valueOf(materialValue);
            } catch (IllegalArgumentException e) {
                throw new CreateEquipmentException("Unable to create equipment item " + equipment.getName() + ", placing material " + materialValue + " is invalid");
            }

            List<GameSound> placeSounds = DefaultGameSound.parseSounds(section.getString("placing.place-sound"));
            long delayAfterPlacement = section.getLong("placing.delay-after-placement");

            PlaceProperties properties = new PlaceProperties(placeSounds, material, delayAfterPlacement);
            PlaceFunction placeFunction = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);

            equipment.getControls().addControl(placeAction, placeFunction);
        }

        if (activateActionValue != null) {
            Action activateAction = this.getActionFromConfiguration("activate", activateActionValue);

            List<GameSound> activationSounds = DefaultGameSound.parseSounds(section.getString("effect.activation.activation-sound"));
            long delayUntilActivation = section.getLong("effect.activation.delay-until-activation");

            ActivateProperties properties = new ActivateProperties(activationSounds, delayUntilActivation);
            ActivateFunction activateFunction = new ActivateFunction(properties, equipment, audioEmitter, taskRunner);

            equipment.getControls().addControl(activateAction, activateFunction);
        }
    }

    @NotNull
    private Action getActionFromConfiguration(@NotNull String functionName, @NotNull String value) {
        try {
            return Action.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new CreateEquipmentException("Error while getting controls for " + functionName + ": \""
                    + value + "\" is not a valid action type!");
        }
    }
}
