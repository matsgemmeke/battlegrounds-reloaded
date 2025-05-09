package nl.matsgemmeke.battlegrounds.item.shoot.fullauto;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.shoot.AutomaticFireCycleRunnable;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullyAutomaticMode implements FireMode {

    @Nullable
    private BukkitTask currentTask;
    private final int rateOfFire;
    @NotNull
    private final Shootable item;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public FullyAutomaticMode(@NotNull TaskRunner taskRunner, @Assisted @NotNull Shootable item, @Assisted int rateOfFire) {
        this.taskRunner = taskRunner;
        this.item = item;
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
