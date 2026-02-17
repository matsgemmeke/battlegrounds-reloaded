package nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter;

import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockTriggerResultAdapter implements TriggerResultAdapter<BlockTriggerResult> {

    @Override
    public CollisionResult adapt(BlockTriggerResult triggerResult) {
        Block hitBlock = triggerResult.getHitBlock();
        Location hitLocation = triggerResult.getHitLocation();

        return new CollisionResult(hitBlock, null, hitLocation);
    }
}
