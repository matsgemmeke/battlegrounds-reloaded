package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnemyProximityTriggerTest {

    private static final UUID SOURCE_ID = UUID.randomUUID();
    private static final double CHECKING_RANGE = 2.5;

    @Mock
    private TargetFinder targetFinder;
    @Mock
    private TriggerTarget target;
    @InjectMocks
    private EnemyProximityTrigger trigger;

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenTriggerTargetDoesNotExist() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(false);

        trigger.setCheckingRange(CHECKING_RANGE);
        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenThereAreNoNearbyEnemyTargets() {
        Location targetLocation = new Location(null, 1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(targetFinder.findEnemyTargets(SOURCE_ID, targetLocation, CHECKING_RANGE)).thenReturn(Collections.emptyList());

        trigger.setCheckingRange(CHECKING_RANGE);
        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatActivatesWhenThereAreNearbyEnemyTargets() {
        Location targetLocation = new Location(null, 1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(targetFinder.findEnemyTargets(SOURCE_ID, targetLocation, CHECKING_RANGE)).thenReturn(List.of(mock(GameEntity.class)));

        trigger.setCheckingRange(CHECKING_RANGE);
        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isTrue();
    }
}
