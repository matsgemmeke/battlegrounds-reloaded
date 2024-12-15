package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullyAutomaticMode implements FireMode {

    @Nullable
    private BukkitTask currentTask;
    private int rateOfFire;
    @NotNull
    private Shootable item;
    @NotNull
    private TaskRunner taskRunner;

    public FullyAutomaticMode(@NotNull Shootable item, @NotNull TaskRunner taskRunner, int rateOfFire) {
        this.item = item;
        this.taskRunner = taskRunner;
        this.rateOfFire = rateOfFire;
    }

    public int getRateOfFire() {
        return rateOfFire;
    }

    public boolean activateCycle() {
        // The amount of interaction events per second received when holding down the right mouse button
        int interactionsPerSecond = 5;
        // The interval between interactions in ticks
        int interactionsInterval = 4;
        // Convert rate of fire to amount of rounds fired per second
        int roundsPerSecond = rateOfFire / 60;
        // Amount of rounds to be fired for one click cycle
        int amountOfRounds = roundsPerSecond / interactionsPerSecond;
        // Amount of ticks between the rounds being fired
        long period = interactionsInterval / amountOfRounds;
        long delay = 0;

        currentTask = taskRunner.runTaskTimer(
                new AutomaticFireCycleRunnable(item, amountOfRounds, this::cancelCycle),
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
