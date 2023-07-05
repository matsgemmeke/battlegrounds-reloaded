package com.github.matsgemmeke.battlegrounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullyAutomaticMode implements FireMode {

    @Nullable
    private BukkitTask currentTask;
    @NotNull
    private Gun gun;
    private int rateOfFire;
    @NotNull
    private TaskRunner taskRunner;

    public FullyAutomaticMode(@NotNull TaskRunner taskRunner, @NotNull Gun gun, int rateOfFire) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.rateOfFire = rateOfFire;
    }

    public boolean activate(@NotNull BattleItemHolder holder) {
        gun.setCurrentOperatingMode(this);

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

        currentTask = taskRunner.runTaskTimer(new AutomaticFireCycleRunnable(gun, amountOfRounds, this::cancel), delay, period);
        return true;
    }

    public void cancel() {
        gun.setCurrentOperatingMode(null);

        if (currentTask == null) {
            return;
        }

        currentTask.cancel();
    }
}
