package nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.BaseFireMode;
import org.jetbrains.annotations.NotNull;

public class SemiAutomaticMode extends BaseFireMode {

    private static final int TICKS_PER_MINUTE = 1200;

    private boolean delaying;
    private final long delayBetweenShots;
    @NotNull
    private final Shootable item;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public SemiAutomaticMode(@NotNull TaskRunner taskRunner, @Assisted @NotNull Shootable item, @Assisted long delayBetweenShots) {
        this.taskRunner = taskRunner;
        this.item = item;
        this.delayBetweenShots = delayBetweenShots;
        this.delaying = false;
    }

    public boolean startCycle() {
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
