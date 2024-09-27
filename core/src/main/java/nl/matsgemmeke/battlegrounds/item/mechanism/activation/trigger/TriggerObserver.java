package nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.jetbrains.annotations.NotNull;

public interface TriggerObserver {

    void onTrigger(@NotNull ItemHolder holder, @NotNull Deployable object);
}
