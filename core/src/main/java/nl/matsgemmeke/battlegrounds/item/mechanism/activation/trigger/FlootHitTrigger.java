package nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FlootHitTrigger implements Trigger {

    private static final long RUNNABLE_DELAY = 0L;

    @Nullable
    private BukkitTask task;
    @NotNull
    private List<TriggerListener> listeners;
    private long checkPeriod;
    @NotNull
    private TaskRunner taskRunner;

    public FlootHitTrigger(@NotNull TaskRunner taskRunner, long checkPeriod) {
        this.taskRunner = taskRunner;
        this.checkPeriod = checkPeriod;
        this.listeners = new ArrayList<>();
    }

    public void addListener(@NotNull TriggerListener listener) {
        listeners.add(listener);
    }

    public void checkTriggerActivation(@NotNull ItemHolder holder, @NotNull Deployable object) {
        task = taskRunner.runTaskTimer(() -> {
            Block blockBelowObject = object.getLocation().getBlock().getRelative(BlockFace.DOWN);

            if (blockBelowObject.isPassable()) {
                return;
            }

            this.notifyListeners(holder, object);
            task.cancel();
        }, RUNNABLE_DELAY, checkPeriod);
    }

    private void notifyListeners(@NotNull ItemHolder holder, @NotNull Deployable object) {
        for (TriggerListener listener : listeners) {
            listener.onTrigger(holder, object);
        }
    }
}
