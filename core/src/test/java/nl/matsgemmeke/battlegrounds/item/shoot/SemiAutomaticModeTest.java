package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SemiAutomaticModeTest {

    private long delayBetweenShots;
    private Shootable item;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        item = mock(Shootable.class);
        taskRunner = mock(TaskRunner.class);
        delayBetweenShots = 1;
    }

    @Test
    public void shouldShootItemOnceWhenActivated() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, delayBetweenShots);
        boolean activated = fireMode.activateCycle();

        verify(item).shoot();

        assertTrue(activated);
    }

    @Test
    public void doNothingIfItemIsCoolingDown() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, delayBetweenShots);
        fireMode.activateCycle();
        boolean activated = fireMode.activateCycle();

        assertFalse(activated);
    }

    @Test
    public void shouldNotCancelIfNotActivated() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, delayBetweenShots);
        boolean cancelled = fireMode.cancelCycle();

        assertFalse(cancelled);
    }

    @Test
    public void shouldResetDelayWhenCancelling() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, delayBetweenShots);
        fireMode.activateCycle();
        boolean cancelled = fireMode.cancelCycle();
        fireMode.activateCycle();

        assertTrue(cancelled);

        verify(item, times(2)).shoot();
    }

    @Test
    public void shouldNeverBeCycling() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, delayBetweenShots);

        assertFalse(fireMode.isCycling());
    }

    @Test
    public void shouldNotBeCyclingAfterActivation() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, delayBetweenShots);
        fireMode.activateCycle();

        assertFalse(fireMode.isCycling());
    }
}
