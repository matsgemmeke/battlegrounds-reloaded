package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.jetbrains.annotations.NotNull;

public interface TriggerObserver {

    void onTrigger(@NotNull ItemEffectContext context);
}
