package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class FloorHitTrigger implements Trigger {

    private static final double Y_SUBTRACTION = 0.01;

    @Override
    public TriggerResult check(TriggerContext context) {
        Actor actor = context.actor();

        if (!actor.exists()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        Location actorLocation = actor.getLocation();
        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = actorLocation.subtract(0, Y_SUBTRACTION, 0).getBlock();

        if (blockBelowObject.isPassable()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        return new BlockTriggerResult(blockBelowObject, actorLocation);
    }
}
