package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.jetbrains.annotations.NotNull;

public interface Trigger {

    void addObserver(@NotNull TriggerObserver observer);

    void cancel();

    void checkTriggerActivation(@NotNull ItemEffectContext context);
}
