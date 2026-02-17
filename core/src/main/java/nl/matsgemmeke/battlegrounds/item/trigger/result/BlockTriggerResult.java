package nl.matsgemmeke.battlegrounds.item.trigger.result;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * A trigger result that carries data of a block that was hit.
 */
public class BlockTriggerResult implements TriggerResult {

    private final Block hitBlock;
    private final Location hitLocation;

    public BlockTriggerResult(Block hitBlock, Location hitLocation) {
        this.hitBlock = hitBlock;
        this.hitLocation = hitLocation;
    }

    @Override
    public boolean activates() {
        return true;
    }

    public Block getHitBlock() {
        return hitBlock;
    }

    public Location getHitLocation() {
        return hitLocation;
    }
}
