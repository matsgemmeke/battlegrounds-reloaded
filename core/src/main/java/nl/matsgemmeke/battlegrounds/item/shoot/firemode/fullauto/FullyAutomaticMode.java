package nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto;

import nl.matsgemmeke.battlegrounds.item.shoot.firemode.BaseFireMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

public class FullyAutomaticMode extends BaseFireMode {

    private boolean cycling;
    @NotNull
    private final Schedule shotSchedule;
    @NotNull
    private final Schedule cooldownSchedule;
    private final int rateOfFire;

    public FullyAutomaticMode(@NotNull Schedule shotSchedule, @NotNull Schedule cooldownSchedule, int rateOfFire) {
        this.shotSchedule = shotSchedule;
        this.cooldownSchedule = cooldownSchedule;
        this.rateOfFire = rateOfFire;
        this.cycling = false;
    }

    public int getRateOfFire() {
        return rateOfFire;
    }

    public boolean isCycling() {
        return cycling;
    }

    public boolean startCycle() {
        if (cycling) {
            return false;
        }

        cycling = true;

        shotSchedule.clearTasks();
        shotSchedule.addTask(this::notifyShotObservers);
        shotSchedule.start();
        cooldownSchedule.clearTasks();
        cooldownSchedule.addTask(this::cancelCycle);
        cooldownSchedule.start();
        return true;
    }

    public boolean cancelCycle() {
        if (!cycling) {
            return false;
        }

        cycling = false;
        shotSchedule.stop();
        cooldownSchedule.stop();
        return true;
    }
}
