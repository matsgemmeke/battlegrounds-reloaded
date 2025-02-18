package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunctionFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateProperties;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class EquipmentControlsFactory {

    @NotNull
    private final ActivateFunctionFactory activateFunctionFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public EquipmentControlsFactory(
            @NotNull ActivateFunctionFactory activateFunctionFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull TaskRunner taskRunner
    ) {
        this.activateFunctionFactory = activateFunctionFactory;
        this.contextProvider = contextProvider;
        this.taskRunner = taskRunner;
    }

    @NotNull
    public ItemControls<EquipmentHolder> create(@NotNull Section section, @NotNull Equipment equipment, @NotNull GameKey gameKey) {
        ItemControls<EquipmentHolder> controls = new ItemControls<>();

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

        Section controlsSection = section.getSection("controls");
        String activateActionValue = controlsSection.getString("activate");
        String cookActionValue = controlsSection.getString("cook");
        String placeActionValue = controlsSection.getString("place");
        String throwActionValue = controlsSection.getString("throw");

        if (throwActionValue != null) {
            Action throwAction = this.getActionFromConfiguration(equipment, "throw", throwActionValue);

            if (cookActionValue != null) {
                Action cookAction = this.getActionFromConfiguration(equipment, "cook", cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(section.getString("throwing.cook-sound"));

                CookProperties cookProperties = new CookProperties(cookSounds);
                CookFunction cookFunction = new CookFunction(cookProperties, equipment, audioEmitter);

                controls.addControl(cookAction, cookFunction);
            }

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(section.getString("throwing.throw-sound"));
            double velocity = section.getDouble("throwing.velocity");
            long delayAfterThrow = section.getLong("throwing.delay-after-throw");

            ThrowProperties properties = new ThrowProperties(throwSounds, velocity, delayAfterThrow);
            ThrowFunction throwFunction = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);

            controls.addControl(throwAction, throwFunction);
        }

        if (placeActionValue != null) {
            Action placeAction = this.getActionFromConfiguration(equipment, "place", placeActionValue);
            Material material = this.getMaterialFromConfiguration(equipment, "place material", section.getString("placing.material"));

            List<GameSound> placeSounds = DefaultGameSound.parseSounds(section.getString("placing.place-sound"));
            long delayAfterPlacement = section.getLong("placing.delay-after-placement");

            PlaceProperties properties = new PlaceProperties(placeSounds, material, delayAfterPlacement);
            PlaceFunction placeFunction = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);

            controls.addControl(placeAction, placeFunction);
        }

        if (activateActionValue != null) {
            Action activateAction = this.getActionFromConfiguration(equipment, "activate", activateActionValue);

            List<GameSound> activationSounds = DefaultGameSound.parseSounds(section.getString("effect.activation.activation-sound"));
            long delayUntilActivation = section.getLong("effect.activation.delay-until-activation");

            ActivateProperties properties = new ActivateProperties(activationSounds, delayUntilActivation);
            ItemFunction<EquipmentHolder> activateFunction = activateFunctionFactory.create(properties, equipment, audioEmitter);

            controls.addControl(activateAction, activateFunction);
        }

        return controls;
    }

    @NotNull
    private Action getActionFromConfiguration(@NotNull Equipment equipment, @NotNull String functionName, @NotNull String value) {
        try {
            return Action.valueOf(value);
        } catch (IllegalArgumentException e) {
            TextTemplate textTemplate = new TextTemplate("Error while creating controls for equipment %equipment_name%: " +
                    "the value \"%action_value%\" for function \"%function_name%\" is not a valid action type!");
            Map<String, Object> values = Map.of(
                    "equipment_name", equipment.getName(),
                    "action_value", value,
                    "function_name", functionName
            );
            String message = textTemplate.replace(values);

            throw new EquipmentControlsCreationException(message);
        }
    }

    @NotNull
    private Material getMaterialFromConfiguration(@NotNull Equipment equipment, @NotNull String role, @NotNull String value) {
        try {
            return Material.valueOf(value);
        } catch (IllegalArgumentException e) {
            TextTemplate textTemplate = new TextTemplate("Error while creating controls for equipment %equipment_name%: " +
                    "the value \"%material_value%\" for the %role% is not a valid material type!");
            Map<String, Object> values = Map.of(
                    "equipment_name", equipment.getName(),
                    "material_value", value,
                    "role", role
            );
            String message = textTemplate.replace(values);

            throw new EquipmentControlsCreationException(message);
        }
    }
}
