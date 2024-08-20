package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.deployment.PlantDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.PlantFunction;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanismFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.CookFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivationFactory;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
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
        EntityRegistry<GameItem, Item> itemRegistry = context.getItemRegistry();

        Section section = configuration.getRoot();

        String name = section.getString("display-name");
        String description = section.getString("description");

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.setName(name);
        equipment.setDescription(description);

        // ItemStack creation
        Material material = Material.getMaterial(section.getString("item.material"));
        short durability = section.getShort("item.durability");

        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability(durability);

        equipment.setItemStack(itemStack);

        // Setting the optional activator item
        Section activatorItemSection = section.getSection("item.activator");

        if (activatorItemSection != null) {
            Material activatorMaterial = Material.getMaterial(activatorItemSection.getString("material"));
            short activatorDurability = activatorItemSection.getShort("durability");

            ItemStack activatorItemStack = new ItemStack(activatorMaterial);
            activatorItemStack.setDurability(activatorDurability);

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
        String plantActionValue = controlsSection.getString("plant");
        String throwActionValue = controlsSection.getString("throw");

        ItemMechanism mechanism = mechanismFactory.make(section.getSection("mechanism"), context);
        ItemMechanismActivation mechanismActivation = mechanismActivationFactory.make(section.getSection("activation"), equipment, mechanism);

        if (throwActionValue != null) {
            Action throwAction = this.getActionFromConfiguration("throw", throwActionValue);

            if (cookActionValue != null) {
                Action cookAction = this.getActionFromConfiguration("cook", cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(section.getString("throwing.cook-sound"));

                CookFunction cookFunction = new CookFunction(mechanismActivation, audioEmitter);
                cookFunction.addSounds(cookSounds);

                equipment.getControls().addControl(cookAction, cookFunction);
            }

            long delayAfterThrow = section.getLong("throwing.delay-after-throw");
            double projectileSpeed = section.getDouble("throwing.projectile-speed");

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(section.getString("throwing.throw-sound"));

            ThrowFunction throwFunction = new ThrowFunction(equipment, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
            throwFunction.addSounds(throwSounds);

            equipment.getControls().addControl(throwAction, throwFunction);
        }

        if (plantActionValue != null) {
            Action plantAction = this.getActionFromConfiguration("plant", plantActionValue);

            Material material;
            String materialValue = section.getString("planting.material");

            try {
                material = Material.valueOf(materialValue);
            } catch (IllegalArgumentException e) {
                throw new CreateEquipmentException("Unable to create equipment item " + equipment.getName() + ", planting material " + materialValue + " is invalid");
            }

            List<GameSound> plantSounds = DefaultGameSound.parseSounds(section.getString("planting.plant-sound"));
            PlantDeployment deployment = new PlantDeployment(material);

            PlantFunction plantFunction = new PlantFunction(equipment, mechanismActivation, deployment, audioEmitter);
            plantFunction.addSounds(plantSounds);

            equipment.getControls().addControl(plantAction, plantFunction);
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
