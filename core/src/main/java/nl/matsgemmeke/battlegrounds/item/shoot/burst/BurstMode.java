package nl.matsgemmeke.battlegrounds.item.shoot.burst;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.shoot.AutomaticFireCycleRunnable;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BurstMode implements FireMode {

    @Nullable
    private BukkitTask currentTask;
    private final int amountOfShots;
    private final int rateOfFire;
    @NotNull
    private final Shootable item;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public BurstMode(@NotNull TaskRunner taskRunner, @Assisted @NotNull Shootable item, @Assisted int amountOfShots, @Assisted int rateOfFire) {
        this.taskRunner = taskRunner;
        this.item = item;
        this.amountOfShots = amountOfShots;
        this.rateOfFire = rateOfFire;
    }

    public int getRateOfFire() {
        return rateOfFire;
    }

    public boolean activateCycle() {
        int ticksPerSecond = 20;
        // Convert rate of fire to amount of rounds fired per second
        int shotsPerSecond = rateOfFire / 60;
        // Amount of ticks between the rounds being fired
        long period = ticksPerSecond / shotsPerSecond;
        long delay = 0;

        currentTask = taskRunner.runTaskTimer(
                new AutomaticFireCycleRunnable(item, amountOfShots, this::cancelCycle),
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
