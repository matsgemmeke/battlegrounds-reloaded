package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class responsible for instantiating {@link ItemMechanismActivation} implementation classes.
 */
public class ItemMechanismActivationFactory {

    @NotNull
    private TaskRunner taskRunner;

    public ItemMechanismActivationFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Creates a new {@link ItemMechanismActivation} instance based on configuration values.
     *
     * @param section the configuration section
     * @param item the droppable item
     * @param mechanism the item mechanism instance
     * @return a new activation instance
     */
    public ItemMechanismActivation make(@NotNull Section section, @NotNull Droppable item,  @NotNull ItemMechanism mechanism) {
        String type = section.getString("type");

        if (type == null) {
            throw new InvalidItemConfigurationException("Equipment activation type must be defined!");
        }

        ItemMechanismActivationType mechanismActivationType;

        try {
            mechanismActivationType = ItemMechanismActivationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Equipment activation type \"" + type + "\" is invalid!");
        }

        switch (mechanismActivationType) {
            case DELAYED -> {
                long delayUntilActivation = section.getLong("delay-until-activation");
                return new DelayedActivation(item, mechanism, taskRunner, delayUntilActivation);
            }
            case MANUAL -> {
                return new ManualActivation(item, mechanism);
            }
        }

        throw new InvalidItemConfigurationException("Unknown equipment activation type \"" + type + "\"!");
    }
}
