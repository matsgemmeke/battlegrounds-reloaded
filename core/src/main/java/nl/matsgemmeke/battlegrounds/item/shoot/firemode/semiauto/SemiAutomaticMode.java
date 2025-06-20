package nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto;

import nl.matsgemmeke.battlegrounds.item.shoot.firemode.BaseFireMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

public class SemiAutomaticMode extends BaseFireMode {

    @NotNull
    private final Schedule cooldownSchedule;
    private final int rateOfFire;
    private boolean cycling;

    public SemiAutomaticMode(@NotNull Schedule cooldownSchedule, int rateOfFire) {
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

        cooldownSchedule.clearTasks();
        cooldownSchedule.addTask(this::cancelCycle);
        cooldownSchedule.start();

        this.notifyShotObservers();
        return true;
    }

    public boolean cancelCycle() {
        if (!cycling) {
            return false;
        }

        cycling = false;
        cooldownSchedule.stop();
        return true;
    }
}
