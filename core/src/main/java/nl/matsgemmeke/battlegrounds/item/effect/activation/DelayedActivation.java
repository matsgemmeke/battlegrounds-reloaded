package nl.matsgemmeke.battlegrounds.item.effect.activation;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Activation that initiates the effect after a specific delay.
 */
public class DelayedActivation implements ItemEffectActivation {

    private boolean primed;
    @Nullable
    private BukkitTask currentTask;
    private final long delayUntilActivation;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public DelayedActivation(@NotNull TaskRunner taskRunner, @Assisted long delayUntilActivation) {
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
        this.primed = false;
    }

    public void cancel() {
        if (!primed || currentTask == null) {
            return;
        }

        primed = false;
        currentTask.cancel();
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime(@NotNull ItemEffectContext context, @NotNull Procedure onActivate) {
        if (primed) {
            return;
        }

        primed = true;

        ItemHolder holder = context.getHolder();
        ItemEffectSource source = context.getSource();

        if (source.isDeployed()) {
            holder.setHeldItem(null);
        }

        currentTask = taskRunner.runTaskLater(onActivate::apply, delayUntilActivation);
    }
}
