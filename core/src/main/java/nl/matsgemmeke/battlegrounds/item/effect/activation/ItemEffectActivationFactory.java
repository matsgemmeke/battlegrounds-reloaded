package nl.matsgemmeke.battlegrounds.item.effect.activation;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameKey;
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
    private final DelayedActivationFactory delayedActivationFactory;
    @NotNull
    private final TriggerFactory triggerFactory;

    @Inject
    public ItemEffectActivationFactory(@NotNull DelayedActivationFactory delayedActivationFactory, @NotNull TriggerFactory triggerFactory) {
        this.delayedActivationFactory = delayedActivationFactory;
        this.triggerFactory = triggerFactory;
    }

    /**
     * Creates a new {@link ItemEffectActivation} instance based on configuration values.
     *
     * @param section the configuration section
     * @return a new activation instance
     */
    public ItemEffectActivation create(@NotNull GameKey gameKey, @NotNull Section section, @Nullable Activator activator) {
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

                return delayedActivationFactory.create(delayUntilActivation);
            }
            case MANUAL -> {
                if (activator == null) {
                    throw new InvalidItemConfigurationException("Manual effect activation requires an activator item!");
                }

                return new ManualActivation(activator);
            }
            case TRIGGER -> {
                TriggerActivation activation = new TriggerActivation();
                Iterable<Map<String, Object>> triggers = (Iterable<Map<String, Object>>) section.get("triggers");

                for (Map<String, Object> triggerConfig : triggers) {
                    activation.addTrigger(triggerFactory.create(gameKey, triggerConfig));
                }

                return activation;
            }
        }

        throw new InvalidItemConfigurationException("Unknown effect activation type \"" + type + "\"!");
    }
}
