package nl.matsgemmeke.battlegrounds.item.effect.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.TriggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory class responsible for instantiating {@link ItemEffectActivation} implementation classes.
 */
public class ItemEffectActivationFactory {

    @NotNull
    private TaskRunner taskRunner;

    public ItemEffectActivationFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Creates a new {@link ItemEffectActivation} instance based on configuration values.
     *
     * @param section the configuration section
     * @return a new activation instance
     */
    public ItemEffectActivationNew make(@NotNull GameContext context, @NotNull Section section, @Nullable Activator activator) {
        String type = section.getString("type");

        if (type == null) {
            throw new InvalidItemConfigurationException("Effect activation type must be defined!");
        }

        ItemEffectActivationType effectActivationType;

        try {
            effectActivationType = ItemEffectActivationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Equipment activation type \"" + type + "\" is invalid!");
        }

        switch (effectActivationType) {
            case DELAYED -> {
                long delayUntilActivation = section.getLong("delay-until-activation");
                return new DelayedActivation(taskRunner, delayUntilActivation);
            }
            case MANUAL -> {
                if (activator == null) {
                    throw new InvalidItemConfigurationException("Manual effect activation requires an activator item!");
                }

                return new ManualActivation(activator);
            }
            case TRIGGER -> {
                TriggerActivation activation = new TriggerActivation();
                TriggerFactory triggerFactory = new TriggerFactory(taskRunner);
                Iterable<Map<String, Object>> triggers = (Iterable<Map<String, Object>>) section.get("triggers");

                for (Map<String, Object> triggerConfig : triggers) {
                    activation.addTrigger(triggerFactory.make(context, triggerConfig));
                }

                return activation;
            }
        }

        throw new InvalidItemConfigurationException("Unknown effect activation type \"" + type + "\"!");
    }
}
