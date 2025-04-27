package nl.matsgemmeke.battlegrounds.item.effect.trigger.timed;

import nl.matsgemmeke.battlegrounds.item.effect.trigger.Trigger;
import org.jetbrains.annotations.NotNull;

public interface TimedTriggerFactory {

    @NotNull
    Trigger create(long delayUntilActivation);
}
