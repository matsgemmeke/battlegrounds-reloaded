package com.github.matsgemmeke.battlegrounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.jetbrains.annotations.NotNull;

public class FullyAutomaticMode implements FiringMode {

    private boolean readyToFire;
    @NotNull
    private Gun gun;
    private int rateOfFire;
    @NotNull
    private TaskRunner taskRunner;

    public FullyAutomaticMode(@NotNull TaskRunner taskRunner, @NotNull Gun gun, int rateOfFire) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.rateOfFire = rateOfFire;
        this.readyToFire = true;
    }

    public void activate() {
        if (!readyToFire) {
            return;
        }

        readyToFire = false;

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

        taskRunner.runTaskTimer(new AutomaticFireCycleRunnable(gun, amountOfRounds, () -> readyToFire = true), delay, period);
    }
}
