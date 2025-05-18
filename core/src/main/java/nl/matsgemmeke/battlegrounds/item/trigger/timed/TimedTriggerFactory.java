package nl.matsgemmeke.battlegrounds.item.trigger.timed;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import org.jetbrains.annotations.NotNull;

public interface TimedTriggerFactory {

    @NotNull
    Trigger create(long delayUntilActivation);
}
