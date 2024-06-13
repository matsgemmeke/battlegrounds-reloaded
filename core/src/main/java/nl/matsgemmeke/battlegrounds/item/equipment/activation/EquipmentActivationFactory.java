package nl.matsgemmeke.battlegrounds.item.equipment.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.equipment.mechanism.EquipmentMechanism;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class responsible for instantiating {@link EquipmentActivation} implementation classes.
 */
public class EquipmentActivationFactory {

    @NotNull
    private TaskRunner taskRunner;

    public EquipmentActivationFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Creates a new {@link EquipmentActivation} instance based on configuration values.
     *
     * @param section the configuration section
     * @return a new activation instance
     */
    public EquipmentActivation make(@NotNull Section section, @NotNull EquipmentMechanism mechanism) {
        String type = section.getString("type");

        if (type == null) {
            throw new InvalidItemConfigurationException("Equipment activation type must be defined!");
        }

        EquipmentActivationType equipmentActivationType;

        try {
            equipmentActivationType = EquipmentActivationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Equipment activation type \"" + type + "\" is invalid!");
        }

        switch (equipmentActivationType) {
            case DELAYED_TRIGGER -> {
                long delayUntilTrigger = section.getLong("delay-until-trigger");
                return new DelayedActivation(mechanism, taskRunner, delayUntilTrigger);
            }
        }

        throw new InvalidItemConfigurationException("Unknown equipment activation type \"" + type + "\"!");
    }
}
