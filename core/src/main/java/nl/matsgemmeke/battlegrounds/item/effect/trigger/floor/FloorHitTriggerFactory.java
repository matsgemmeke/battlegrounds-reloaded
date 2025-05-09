package nl.matsgemmeke.battlegrounds.item.effect.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.effect.trigger.Trigger;
import org.jetbrains.annotations.NotNull;

public interface FloorHitTriggerFactory {

    @NotNull
    Trigger create(long periodBetweenChecks);
}
