package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class CollisionResultMapperTest {

    private final CollisionResultMapper mapper = new CollisionResultMapper();

    @Test
    void mapThrowsIllegalStateExceptionWhenGivenTriggerResultTypeIsNotRegistered() {
        UnregisteredTriggerResult triggerResult = new UnregisteredTriggerResult();

        assertThatThrownBy(() -> mapper.map(triggerResult))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No TriggerResultAdapter registered for UnregisteredTriggerResult");
    }

    @Test
    void mapReturnsCollisionResultConvertedFromGivenDamageTargetTriggerResult() {
        DamageTarget hitTarget = mock(DamageTarget.class);
        Location hitLocation = new Location(null, 1, 1, 1);
        DamageTargetTriggerResult triggerResult = new DamageTargetTriggerResult(hitTarget, hitLocation);

        CollisionResult collisionResult = mapper.map(triggerResult);

        assertThat(collisionResult.getHitBlock()).isEmpty();
        assertThat(collisionResult.getHitTarget()).hasValue(hitTarget);
        assertThat(collisionResult.getHitLocation()).hasValue(hitLocation);
    }
}
