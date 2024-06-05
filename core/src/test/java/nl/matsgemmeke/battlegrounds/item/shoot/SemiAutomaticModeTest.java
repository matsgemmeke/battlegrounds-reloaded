package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SemiAutomaticModeTest {

    private long cooldownDuration;
    private Shootable item;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        item = mock(Shootable.class);
        taskRunner = mock(TaskRunner.class);
        cooldownDuration = 1;
    }

    @Test
    public void shouldShootItemOnceWhenActivated() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, cooldownDuration);
        boolean activated = fireMode.activateCycle();

        verify(item).shoot();

        assertTrue(activated);
    }

    @Test
    public void doNothingIfItemIsCoolingDown() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, cooldownDuration);
        fireMode.activateCycle();
        boolean activated = fireMode.activateCycle();

        assertFalse(activated);
    }

    @Test
    public void shouldNotCancelIfNotActivated() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, cooldownDuration);
        boolean cancelled = fireMode.cancel();

        assertFalse(cancelled);
    }

    @Test
    public void shouldResetCooldownWhenCancelling() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, cooldownDuration);
        fireMode.activateCycle();
        boolean cancelled = fireMode.cancel();
        fireMode.activateCycle();

        assertTrue(cancelled);

        verify(item, times(2)).shoot();
    }

    @Test
    public void shouldNeverBeCycling() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, cooldownDuration);

        assertFalse(fireMode.isCycling());
    }

    @Test
    public void shouldNotBeCyclingAfterActivation() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, cooldownDuration);
        fireMode.activateCycle();

        assertFalse(fireMode.isCycling());
    }
}
