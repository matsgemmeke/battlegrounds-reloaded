package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that initiates the effect after a specific delay.
 */
public class DelayedActivation implements ItemEffectActivation {

    private long delayUntilActivation;
    @NotNull
    private TaskRunner taskRunner;

    public DelayedActivation(@NotNull TaskRunner taskRunner, long delayUntilActivation) {
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
    }

    public void prime(@NotNull ItemEffectContext context, @NotNull Procedure onActivate) {
        ItemHolder holder = context.getHolder();
        EffectSource source = context.getSource();

        if (source.isDeployed()) {
            holder.setHeldItem(null);
        }

        taskRunner.runTaskLater(onActivate::apply, delayUntilActivation);
    }
}
