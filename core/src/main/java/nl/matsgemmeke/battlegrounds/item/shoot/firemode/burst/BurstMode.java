package nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst;

import nl.matsgemmeke.battlegrounds.item.shoot.firemode.BaseFireMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

public class BurstMode extends BaseFireMode {

    @NotNull
    private final Schedule shotSchedule;
    @NotNull
    private final Schedule cooldownSchedule;
    private final int amountOfShots;
    private final int rateOfFire;
    private boolean cycling;
    private int shotCount;

    public BurstMode(@NotNull Schedule shotSchedule, @NotNull Schedule cooldownSchedule, int amountOfShots, int rateOfFire) {
        this.shotSchedule = shotSchedule;
        this.cooldownSchedule = cooldownSchedule;
        this.amountOfShots = amountOfShots;
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
        shotCount = 0;

        shotSchedule.clearTasks();
        shotSchedule.addTask(this::fireShot);
        shotSchedule.start();
        cooldownSchedule.clearTasks();
        cooldownSchedule.addTask(this::cancelCycle);
        cooldownSchedule.start();
        return true;
    }

    private void fireShot() {
        shotCount++;
        this.notifyShotObservers();

        if (shotCount >= amountOfShots) {
            shotSchedule.stop();
        }
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
