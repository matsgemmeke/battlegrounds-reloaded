package com.github.matsgemmeke.battlegrounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class AutomaticFireCycleRunnable extends BukkitRunnable {

    @NotNull
    private Firearm firearm;
    private int amountOfShots;
    private int shots;
    @NotNull
    private Procedure onCycleFinish;

    public AutomaticFireCycleRunnable(@NotNull Firearm firearm, int amountOfShots, @NotNull Procedure onCycleFinish) {
        this.firearm = firearm;
        this.amountOfShots = amountOfShots;
        this.onCycleFinish = onCycleFinish;
        this.shots = 0;
    }

    public void run() {
        shots++;

        firearm.shoot();
        firearm.update();

        if (firearm.getMagazineAmmo() <= 0 || shots >= amountOfShots) {
            onCycleFinish.run();
            this.cancel();
        }
    }
}
