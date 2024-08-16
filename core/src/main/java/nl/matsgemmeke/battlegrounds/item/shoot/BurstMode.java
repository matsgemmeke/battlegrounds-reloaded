package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BurstMode implements FireMode {

    @Nullable
    private BukkitTask currentTask;
    private int rateOfFire;
    private int shotAmount;
    @NotNull
    private Shootable item;
    @NotNull
    private TaskRunner taskRunner;

    public BurstMode(@NotNull Shootable item, @NotNull TaskRunner taskRunner, int shotAmount, int rateOfFire) {
        this.item = item;
        this.taskRunner = taskRunner;
        this.shotAmount = shotAmount;
        this.rateOfFire = rateOfFire;
    }

    public boolean activateCycle() {
        int ticksPerSecond = 20;
        // Convert rate of fire to amount of rounds fired per second
        int shotsPerSecond = rateOfFire / 60;
        // Amount of ticks between the rounds being fired
        long period = ticksPerSecond / shotsPerSecond;
        long delay = 0;

        currentTask = taskRunner.runTaskTimer(
                new AutomaticFireCycleRunnable(item, shotAmount, this::cancelCycle),
                delay,
                period
        );
        return true;
    }

    public boolean cancelCycle() {
        if (currentTask == null) {
            return false;
        }

        currentTask.cancel();
        currentTask = null;

        return true;
    }

    public boolean isCycling() {
        return currentTask != null;
    }
}
