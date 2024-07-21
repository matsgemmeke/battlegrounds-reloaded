package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that triggers the mechanism after a specific delay.
 */
public class DelayedActivation implements ItemMechanismActivation {

    private boolean primed;
    @NotNull
    private Droppable item;
    @NotNull
    private ItemMechanism mechanism;
    private long delayUntilTrigger;
    @NotNull
    private TaskRunner taskRunner;

    public DelayedActivation(
            @NotNull Droppable item,
            @NotNull ItemMechanism mechanism,
            @NotNull TaskRunner taskRunner,
            long delayUntilTrigger
    ) {
        this.item = item;
        this.mechanism = mechanism;
        this.taskRunner = taskRunner;
        this.delayUntilTrigger = delayUntilTrigger;
        this.primed = false;
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime(@NotNull ItemHolder holder) {
        primed = true;

        taskRunner.runTaskLater(() -> mechanism.activate(item.getDroppedItem(), holder), delayUntilTrigger);
    }
}
