package nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TriggerFactoryTest {

    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        taskRunner = mock(TaskRunner.class);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void throwExceptionWhenGivenTypeIsInvalid() {
        Map<String, Object> triggerConfig = Map.of("type", "fail");

        TriggerFactory factory = new TriggerFactory(taskRunner);
        factory.make(triggerConfig);
    }

    @Test
    public void createInstanceOfFloorHitTrigger() {
        Map<String, Object> triggerConfig = Map.of(
                "type", "FLOOR_HIT",
                "period-between-checks", 10
        );

        TriggerFactory factory = new TriggerFactory(taskRunner);
        Trigger trigger = factory.make(triggerConfig);

        assertTrue(trigger instanceof FloorHitTrigger);
    }
}
