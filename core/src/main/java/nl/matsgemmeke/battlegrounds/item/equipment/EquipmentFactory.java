package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
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
    public Equipment make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context) {
        Equipment equipment = this.createInstance(configuration, context);

        game.getEquipmentStorage().addUnassignedItem(equipment);

        return equipment;
    }

    @NotNull
    public Equipment make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(configuration, context);
        equipment.setHolder(gamePlayer);

        game.getEquipmentStorage().addAssignedItem(equipment, gamePlayer);

        return equipment;
    }

    @NotNull
    private Equipment createInstance(@NotNull ItemConfiguration configuration, @NotNull GameContext context) {
        EntityRegistry<Item, GameItem> itemRegistry = context.getItemRegistry();

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

        // Read controls configuration
        Section controlsSection = section.getSection("controls");

        if (controlsSection != null) {
            this.addControls(equipment, context, section, controlsSection);
        }

        return equipment;
    }

    private void addControls(@NotNull DefaultEquipment equipment, @NotNull GameContext context, @NotNull Section section, @NotNull Section controlsSection) {
        AudioEmitter audioEmitter = context.getAudioEmitter();

        String cookActionValue = controlsSection.getString("cook");
        String throwActionValue = controlsSection.getString("throw");

        if (throwActionValue != null) {
            Action throwAction = this.getActionFromConfiguration("throw", throwActionValue);

            ItemMechanism mechanism = mechanismFactory.make(section.getSection("mechanism"));
            ItemMechanismActivation activation = mechanismActivationFactory.make(section.getSection("activation"), equipment, mechanism);

            if (cookActionValue != null) {
                Action cookAction = this.getActionFromConfiguration("cook", cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(section.getString("throwing.cook-sound"));

                CookFunction cookFunction = new CookFunction(activation, audioEmitter);
                cookFunction.addSounds(cookSounds);

                equipment.getControls().addControl(cookAction, cookFunction);
            }

            long delayBetweenThrows = section.getLong("throwing.delay-between-throws");
            double projectileSpeed = section.getDouble("throwing.projectile-speed");

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(section.getString("throwing.throw-sound"));

            ThrowFunction throwFunction = new ThrowFunction(equipment, activation, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
            throwFunction.addSounds(throwSounds);

            equipment.getControls().addControl(throwAction, throwFunction);
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
