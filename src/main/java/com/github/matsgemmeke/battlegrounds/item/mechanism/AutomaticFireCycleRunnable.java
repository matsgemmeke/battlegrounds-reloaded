package com.github.matsgemmeke.battlegrounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class AutomaticFireCycleRunnable extends BukkitRunnable {

    @NotNull
    private Gun gun;
    private int amountOfShots;
    private int shots;
    @NotNull
    private Procedure onCycleFinish;

    public AutomaticFireCycleRunnable(@NotNull Gun gun, int amountOfShots, @NotNull Procedure onCycleFinish) {
        this.gun = gun;
        this.amountOfShots = amountOfShots;
        this.onCycleFinish = onCycleFinish;
        this.shots = 0;
    }

    public void run() {
        shots++;

        gun.shoot();

        if (gun.getMagazineAmmo() <= 0 || shots >= amountOfShots) {
            onCycleFinish.run();
            this.cancel();
        }
    }
}
