package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.jetbrains.annotations.NotNull;

public class SemiAutomaticMode implements FireMode {

    private static final int TICKS_PER_MINUTE = 1200;

    private boolean delaying;
    private long delayBetweenShots;
    @NotNull
    private Shootable item;
    @NotNull
    private TaskRunner taskRunner;

    public SemiAutomaticMode(@NotNull Shootable item, @NotNull TaskRunner taskRunner, long delayBetweenShots) {
        this.item = item;
        this.taskRunner = taskRunner;
        this.delayBetweenShots = delayBetweenShots;
        this.delaying = false;
    }

    public boolean activateCycle() {
        if (delaying) {
            return false;
        }

        delaying = true;
        item.shoot();
        taskRunner.runTaskLater(this::cancelCycle, delayBetweenShots);
        return true;
    }

    public boolean cancelCycle() {
        if (!delaying) {
            return false;
        }

        delaying = false;
        return true;
    }

    public int getRateOfFire() {
        return Math.floorDiv(TICKS_PER_MINUTE, (int) delayBetweenShots + 1);
    }

    public boolean isCycling() {
        return false;
    }
}
