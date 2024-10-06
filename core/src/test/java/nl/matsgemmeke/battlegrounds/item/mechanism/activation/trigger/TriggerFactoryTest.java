package nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerFactoryTest {

    private GameContext context;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void throwExceptionWhenGivenTypeIsInvalid() {
        Map<String, Object> triggerConfig = Map.of("type", "fail");

        TriggerFactory factory = new TriggerFactory(taskRunner);
        factory.make(context, triggerConfig);
    }

    @Test
    public void createInstanceOfEnemyProximityTrigger() {
        Map<String, Object> triggerConfig = Map.of(
                "type", "ENEMY_PROXIMITY",
                "checking-range", 2.5,
                "period-between-checks", 10
        );

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        TriggerFactory factory = new TriggerFactory(taskRunner);
        Trigger trigger = factory.make(context, triggerConfig);

        assertTrue(trigger instanceof EnemyProximityTrigger);
    }

    @Test
    public void createInstanceOfFloorHitTrigger() {
        Map<String, Object> triggerConfig = Map.of(
                "type", "FLOOR_HIT",
                "period-between-checks", 10
        );

        TriggerFactory factory = new TriggerFactory(taskRunner);
        Trigger trigger = factory.make(context, triggerConfig);

        assertTrue(trigger instanceof FloorHitTrigger);
    }
}
