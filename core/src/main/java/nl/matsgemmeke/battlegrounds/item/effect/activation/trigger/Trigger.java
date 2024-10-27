package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;

public interface Trigger {

    void addObserver(@NotNull TriggerObserver observer);

    void checkTriggerActivation(@NotNull ItemHolder holder, @NotNull Deployable object);

    void checkTriggerActivation(@NotNull ItemHolder holder, @NotNull EffectSource source);
}
