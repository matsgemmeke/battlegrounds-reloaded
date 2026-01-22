package nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter;

import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BlockTriggerResultAdapterTest {

    @Test
    void adaptReturnsCollisionResultWithValuesFromGivenSimpleTriggerResult() {
        Block hitBlock = mock(Block.class);
        Location hitLocation = new Location(null, 1, 1, 1);
        BlockTriggerResult triggerResult = new BlockTriggerResult(hitBlock, hitLocation);

        BlockTriggerResultAdapter adapter = new BlockTriggerResultAdapter();
        CollisionResult collisionResult = adapter.adapt(triggerResult);

        assertThat(collisionResult.getHitBlock()).hasValue(hitBlock);
        assertThat(collisionResult.getHitTarget()).isEmpty();
        assertThat(collisionResult.getHitLocation()).hasValue(hitLocation);
    }
}
