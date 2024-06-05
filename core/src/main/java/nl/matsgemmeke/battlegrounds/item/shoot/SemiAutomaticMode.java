package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.jetbrains.annotations.NotNull;

public class SemiAutomaticMode implements FireMode {

    private boolean coolingDown;
    private long cooldownDuration;
    @NotNull
    private Shootable item;
    @NotNull
    private TaskRunner taskRunner;

    public SemiAutomaticMode(@NotNull Shootable item, @NotNull TaskRunner taskRunner, long cooldownDuration) {
        this.item = item;
        this.taskRunner = taskRunner;
        this.cooldownDuration = cooldownDuration;
        this.coolingDown = false;
    }

    public boolean activateCycle() {
        if (coolingDown) {
            return false;
        }

        coolingDown = true;
        item.shoot();
        taskRunner.runTaskLater(this::cancel, cooldownDuration);
        return true;
    }

    public boolean cancel() {
        if (!coolingDown) {
            return false;
        }

        coolingDown = false;
        return true;
    }

    public boolean isCycling() {
        return false;
    }
}
