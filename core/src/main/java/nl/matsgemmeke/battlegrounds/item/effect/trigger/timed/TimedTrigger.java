package nl.matsgemmeke.battlegrounds.item.effect.trigger.timed;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.BaseTrigger;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimedTrigger extends BaseTrigger {

    private boolean primed;
    @Nullable
    private BukkitTask currentTask;
    private final long delayUntilActivation;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public TimedTrigger(@NotNull TaskRunner taskRunner, @Assisted long delayUntilActivation) {
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

    public void prime(@NotNull ItemEffectContext context) {
        if (primed) {
            return;
        }

        primed = true;

        Deployer deployer = context.getDeployer();
        ItemEffectSource source = context.getSource();

        if (source.isDeployed()) {
            deployer.setHeldItem(null);
        }

        currentTask = taskRunner.runTaskLater(this::notifyObservers, delayUntilActivation);
    }
}
