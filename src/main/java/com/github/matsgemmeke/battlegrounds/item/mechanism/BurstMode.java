package com.github.matsgemmeke.battlegrounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import org.jetbrains.annotations.NotNull;

public class BurstMode implements FiringMode {

    private boolean readyToFire;
    @NotNull
    private Firearm firearm;
    private int rateOfFire;
    private int shotAmount;
    @NotNull
    private TaskRunner taskRunner;

    public BurstMode(@NotNull TaskRunner taskRunner, @NotNull Firearm firearm, int shotAmount, int rateOfFire) {
        this.taskRunner = taskRunner;
        this.firearm = firearm;
        this.shotAmount = shotAmount;
        this.rateOfFire = rateOfFire;
        this.readyToFire = true;
    }

    public void activate() {
        if (!readyToFire) {
            return;
        }

        readyToFire = false;

        int ticksPerSecond = 20;
        // Convert rate of fire to amount of rounds fired per second
        int shotsPerSecond = rateOfFire / 60;
        // Amount of ticks between the rounds being fired
        long period = ticksPerSecond / shotsPerSecond;
        long delay = 0;

        taskRunner.runTaskTimer(new AutomaticFireCycleRunnable(firearm, shotAmount, () -> readyToFire = true), delay, period);
    }
}
