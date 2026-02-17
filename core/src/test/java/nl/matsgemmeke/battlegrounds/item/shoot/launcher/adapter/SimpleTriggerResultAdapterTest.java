package nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter;

import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTriggerResultAdapterTest {

    @Test
    void adaptReturnsCollisionResultWithValuesFromGivenSimpleTriggerResult() {
        SimpleTriggerResult triggerResult = SimpleTriggerResult.ACTIVATES;

        SimpleTriggerResultAdapter adapter = new SimpleTriggerResultAdapter();
        CollisionResult collisionResult = adapter.adapt(triggerResult);

        assertThat(collisionResult.getHitBlock()).isEmpty();
        assertThat(collisionResult.getHitTarget()).isEmpty();
        assertThat(collisionResult.getHitLocation()).isEmpty();
    }
}
