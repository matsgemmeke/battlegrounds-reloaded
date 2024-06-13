package nl.matsgemmeke.battlegrounds.item.equipment.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.equipment.mechanism.EquipmentMechanism;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DelayedActivationTest {

    private EquipmentMechanism mechanism;
    private long delayUntilTrigger;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        mechanism = mock(EquipmentMechanism.class);
        taskRunner = mock(TaskRunner.class);
        delayUntilTrigger = 1L;
    }

    @Test
    public void shouldScheduleDelayedTaskWhenPriming() {
        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilTrigger);
        activation.prime();
        boolean primed = activation.isPrimed();

        assertTrue(primed);

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayUntilTrigger));
    }
}
