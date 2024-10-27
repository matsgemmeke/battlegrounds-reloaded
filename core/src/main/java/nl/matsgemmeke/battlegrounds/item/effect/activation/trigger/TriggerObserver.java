package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;

public interface TriggerObserver {

    void onTrigger(@NotNull ItemHolder holder, @NotNull EffectSource source);
}
