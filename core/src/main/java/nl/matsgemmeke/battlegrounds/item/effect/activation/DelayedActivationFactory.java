package nl.matsgemmeke.battlegrounds.item.effect.activation;

import org.jetbrains.annotations.NotNull;

public interface DelayedActivationFactory {

    @NotNull
    ItemEffectActivation create(long delayUntilActivation);
}
