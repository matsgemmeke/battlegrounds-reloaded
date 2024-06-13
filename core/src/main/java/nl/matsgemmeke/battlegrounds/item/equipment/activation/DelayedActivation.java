package nl.matsgemmeke.battlegrounds.item.equipment.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.equipment.mechanism.EquipmentMechanism;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that triggers the mechanism after a specific delay.
 */
public class DelayedActivation implements EquipmentActivation {

    private boolean primed;
    @NotNull
    private EquipmentMechanism mechanism;
    private long delayUntilTrigger;
    @NotNull
    private TaskRunner taskRunner;

    public DelayedActivation(@NotNull EquipmentMechanism mechanism, @NotNull TaskRunner taskRunner, long delayUntilTrigger) {
        this.mechanism = mechanism;
        this.taskRunner = taskRunner;
        this.delayUntilTrigger = delayUntilTrigger;
        this.primed = false;
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime() {
        primed = true;

        taskRunner.runTaskLater(() -> mechanism.activate(), delayUntilTrigger);
    }
}
