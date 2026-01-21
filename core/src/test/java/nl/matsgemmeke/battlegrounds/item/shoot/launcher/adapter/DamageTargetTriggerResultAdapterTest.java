package nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DamageTargetTriggerResultAdapterTest {

    @Test
    void adaptReturnsCollisionResultWithValuesFromGivenTriggerResult() {
        DamageTarget hitTarget = mock(DamageTarget.class);
        Location hitLocation = new Location(null, 1, 1, 1);
        DamageTargetTriggerResult triggerResult = new DamageTargetTriggerResult(hitTarget, hitLocation);

        DamageTargetTriggerResultAdapter adapter = new DamageTargetTriggerResultAdapter();
        CollisionResult collisionResult = adapter.adapt(triggerResult);

        assertThat(collisionResult.getHitBlock()).isEmpty();
        assertThat(collisionResult.getHitTarget()).hasValue(hitTarget);
        assertThat(collisionResult.getHitLocation()).hasValue(hitLocation);
    }
}
