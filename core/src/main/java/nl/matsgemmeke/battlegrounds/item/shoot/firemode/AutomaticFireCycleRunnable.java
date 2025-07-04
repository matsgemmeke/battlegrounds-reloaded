package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class AutomaticFireCycleRunnable extends BukkitRunnable {

    private int amountOfShots;
    private int shots;
    @NotNull
    private Procedure onCycleFinish;
    @NotNull
    private Shootable item;

    public AutomaticFireCycleRunnable(@NotNull Shootable item, int amountOfShots, @NotNull Procedure onCycleFinish) {
        this.item = item;
        this.amountOfShots = amountOfShots;
        this.onCycleFinish = onCycleFinish;
        this.shots = 0;
    }

    public void run() {
        shots++;

        item.shoot();

        if (shots >= amountOfShots || !item.canShoot()) {
            onCycleFinish.apply();
            this.cancel();
        }
    }
}
