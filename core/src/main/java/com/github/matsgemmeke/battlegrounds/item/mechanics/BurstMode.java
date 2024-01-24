package com.github.matsgemmeke.battlegrounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BurstMode implements FireMode {

    @Nullable
    private BukkitTask currentTask;
    @NotNull
    private Gun gun;
    private int rateOfFire;
    private int shotAmount;
    @NotNull
    private TaskRunner taskRunner;

    public BurstMode(@NotNull TaskRunner taskRunner, @NotNull Gun gun, int shotAmount, int rateOfFire) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.shotAmount = shotAmount;
        this.rateOfFire = rateOfFire;
    }

    public boolean activate(@NotNull ItemHolder holder) {
        gun.setOperatingMode(this);

        int ticksPerSecond = 20;
        // Convert rate of fire to amount of rounds fired per second
        int shotsPerSecond = rateOfFire / 60;
        // Amount of ticks between the rounds being fired
        long period = ticksPerSecond / shotsPerSecond;
        long delay = 0;

        currentTask = taskRunner.runTaskTimer(
                new AutomaticFireCycleRunnable(gun, shotAmount, () -> this.cancel(holder)),
                delay,
                period
        );
        return true;
    }

    public void cancel(@NotNull ItemHolder holder) {
        gun.setOperatingMode(null);

        if (currentTask == null) {
            return;
        }

        currentTask.cancel();
    }
}
