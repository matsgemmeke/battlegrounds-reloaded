package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.block.Block;

public class FloorHitTrigger implements Trigger {

    private static final double Y_SUBTRACTION = 0.01;

    @Override
    public TriggerResult check(TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = target.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

        if (blockBelowObject.isPassable()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        return new BlockTriggerResult(blockBelowObject, target.getLocation());
    }
}
