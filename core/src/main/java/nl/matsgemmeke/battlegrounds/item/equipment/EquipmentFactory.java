package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.PlaceFunction;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanismFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.CookFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivationFactory;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EquipmentFactory implements WeaponFactory {

    @NotNull
    private ItemMechanismFactory mechanismFactory;
    @NotNull
    private ItemMechanismActivationFactory mechanismActivationFactory;
    @NotNull
    private TaskRunner taskRunner;

    public EquipmentFactory(
            @NotNull ItemMechanismFactory mechanismFactory,
            @NotNull ItemMechanismActivationFactory mechanismActivationFactory,
            @NotNull TaskRunner taskRunner
    ) {
        this.mechanismFactory = mechanismFactory;
        this.mechanismActivationFactory = mechanismActivationFactory;
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

        int damage = section.getInt("item.damage");
        String displayName = section.getString("item.display-name");

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
            itemStack.setItemMeta(itemMeta);
        }

        if (displayName != null) {
            TextTemplate displayNameTemplate = new TextTemplate(ChatColor.translateAlternateColorCodes('&', displayName));

            equipment.setDisplayNameTemplate(displayNameTemplate);
        }

        equipment.setItemStack(itemStack);
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

            int activatorDamage = activatorItemSection.getInt("damage");
            String activatorDisplayName = activatorItemSection.getString("display-name");

            ItemStack activatorItemStack = new ItemStack(activatorMaterial);
            ItemMeta activatorItemMeta = activatorItemStack.getItemMeta();

            if (activatorItemMeta instanceof Damageable) {
                ((Damageable) activatorItemMeta).setDamage(activatorDamage);
            }

            if (activatorDisplayName != null) {
                TextTemplate activatorDisplayNameTemplate = new TextTemplate(ChatColor.translateAlternateColorCodes('&', activatorDisplayName));

                activatorItemMeta.setDisplayName(activatorDisplayNameTemplate.getText());
            }

            activatorItemStack.setItemMeta(activatorItemMeta);
            equipment.setActivatorItemStack(activatorItemStack);
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

        ItemMechanism mechanism = mechanismFactory.make(section.getSection("mechanism"), context);
        ItemMechanismActivation mechanismActivation = mechanismActivationFactory.make(context, mechanism, section.getSection("activation"));

        if (throwActionValue != null) {
            Action throwAction = this.getActionFromConfiguration("throw", throwActionValue);

            if (cookActionValue != null) {
                Action cookAction = this.getActionFromConfiguration("cook", cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(section.getString("throwing.cook-sound"));

                CookFunction cookFunction = new CookFunction(mechanismActivation, audioEmitter);
                cookFunction.addSounds(cookSounds);

                equipment.getControls().addControl(cookAction, cookFunction);
            }

            Material material;
            String materialValue = section.getString("item.throw-item.material");

            try {
                material = Material.valueOf(materialValue);
            } catch (IllegalArgumentException e) {
                throw new CreateEquipmentException("Unable to create equipment item " + equipment.getName() + ", throwing material " + materialValue + " is invalid");
            }

            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta instanceof Damageable) {
                ((Damageable) itemMeta).setDamage(section.getInt("item.throw-item.damage"));
                itemStack.setItemMeta(itemMeta);
            }

            long delayAfterThrow = section.getLong("throwing.delay-after-throw");
            double projectileSpeed = section.getDouble("throwing.projectile-speed");

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(section.getString("throwing.throw-sound"));

            ThrowFunction throwFunction = new ThrowFunction(equipment, itemStack, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
            throwFunction.addSounds(throwSounds);

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

            PlaceFunction placeFunction = new PlaceFunction(equipment, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
            placeFunction.addSounds(placeSounds);

            equipment.getControls().addControl(placeAction, placeFunction);
        }

        if (activateActionValue != null) {
            Action activateAction = this.getActionFromConfiguration("activate", activateActionValue);

            long delayUntilActivation = section.getLong("activation.delay-until-activation");

            List<GameSound> activateSounds = DefaultGameSound.parseSounds(section.getString("activation.activation-sound"));

            ActivateFunction activateFunction = new ActivateFunction(equipment, mechanismActivation, audioEmitter, taskRunner, delayUntilActivation);
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
