package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.activation.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivationFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.*;
import nl.matsgemmeke.battlegrounds.item.projectile.Stickable;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import nl.matsgemmeke.battlegrounds.util.UUIDGenerator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        // Settings the effect activation
        Section effectSection = section.getSection("effect");
        Section effectActivationSection = section.getSection("activation");

        if (effectSection != null && effectActivationSection != null) {
            Activator activator = equipment.getActivator();

            ItemEffect effect = effectFactory.make(effectSection, context);
            ItemEffectActivation activation = effectActivationFactory.make(context, effect, effectActivationSection, activator);

            equipment.setEffectActivation(activation);
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

        ItemEffect effect = effectFactory.make(section.getSection("effect"), context);
        Section activationSection = section.getSection("activation");
        Activator activator = equipment.getActivator();

        ItemEffectActivation activation = effectActivationFactory.make(context, effect, activationSection, activator);

        if (throwActionValue != null) {
            Action throwAction = this.getActionFromConfiguration("throw", throwActionValue);

            if (cookActionValue != null) {
                Action cookAction = this.getActionFromConfiguration("cook", cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(section.getString("throwing.cook-sound"));

                CookFunction cookFunction = new CookFunction(activation, audioEmitter);
                cookFunction.addSounds(cookSounds);

                equipment.getControls().addControl(cookAction, cookFunction);
            }

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(section.getString("throwing.throw-sound"));
            double velocity = section.getDouble("throwing.velocity");
            long delayAfterThrow = section.getLong("throwing.delay-after-throw");

            ThrowProperties throwProperties = new ThrowProperties(throwSounds, velocity, delayAfterThrow);
            ThrowFunction throwFunction = new ThrowFunction(throwProperties, equipment, audioEmitter, taskRunner);

            Section stickableSection = section.getSection("throwing.projectile.stickable");

            if (stickableSection != null) {
                long checkDelay = stickableSection.getLong("check-delay");
                long checkPeriod = stickableSection.getLong("check-period");

                Stickable stickable = new Stickable(taskRunner, checkDelay, checkPeriod);

                throwFunction.addProjectileProperties(stickable);
            }

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

            long delayAfterPlacement = section.getLong("placing.delay-after-placement");

            List<GameSound> placeSounds = DefaultGameSound.parseSounds(section.getString("placing.place-sound"));

            PlaceFunction placeFunction = new PlaceFunction(activation, material, audioEmitter, taskRunner, delayAfterPlacement);
            placeFunction.addSounds(placeSounds);

            equipment.getControls().addControl(placeAction, placeFunction);
        }

        if (activateActionValue != null) {
            Action activateAction = this.getActionFromConfiguration("activate", activateActionValue);

            long delayUntilActivation = section.getLong("activation.delay-until-activation");

            List<GameSound> activateSounds = DefaultGameSound.parseSounds(section.getString("activation.activation-sound"));

            ActivateFunction activateFunction = new ActivateFunction(activation, audioEmitter, taskRunner, delayUntilActivation);
            activateFunction.addSounds(activateSounds);

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
