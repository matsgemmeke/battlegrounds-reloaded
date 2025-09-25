package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerNew;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.block.Block;

public class FloorHitTrigger implements TriggerNew {

    private static final double Y_SUBTRACTION = 0.01;

    public boolean activates(TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return false;
        }

        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = target.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

        return !blockBelowObject.isPassable();
    }
}
