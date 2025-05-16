package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import org.jetbrains.annotations.NotNull;

public interface FloorHitTriggerFactory {

    @NotNull
    Trigger create(long periodBetweenChecks);
}
