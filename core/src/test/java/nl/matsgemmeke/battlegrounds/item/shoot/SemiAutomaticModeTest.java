package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class SemiAutomaticModeTest {

    private static final long DELAY_BETWEEN_SHOTS = 1L;

    private Shootable item;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        item = mock(Shootable.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldShootItemOnceWhenActivated() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, DELAY_BETWEEN_SHOTS);
        boolean activated = fireMode.activateCycle();

        verify(item).shoot();
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_BETWEEN_SHOTS));

        assertTrue(activated);
    }

    @Test
    public void doNothingIfItemIsCoolingDown() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, DELAY_BETWEEN_SHOTS);
        fireMode.activateCycle();
        boolean activated = fireMode.activateCycle();

        assertFalse(activated);

        verify(item, times(1)).shoot();
        verify(taskRunner, times(1)).runTaskLater(any(Runnable.class), eq(DELAY_BETWEEN_SHOTS));
    }

    @Test
    public void shouldNotCancelIfNotActivated() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, DELAY_BETWEEN_SHOTS);
        boolean cancelled = fireMode.cancelCycle();

        assertFalse(cancelled);
    }

    @Test
    public void shouldResetDelayWhenCancelling() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, DELAY_BETWEEN_SHOTS);
        fireMode.activateCycle();
        boolean cancelled = fireMode.cancelCycle();
        fireMode.activateCycle();

        assertTrue(cancelled);

        verify(item, times(2)).shoot();
    }

    @NotNull
    private static Stream<Arguments> rateOfFireExpectations() {
        return Stream.of(
                arguments(0, 1200),
                arguments(1, 600),
                arguments(2, 400),
                arguments(3, 300),
                arguments(9, 120)
        );
    }

    @ParameterizedTest
    @MethodSource("rateOfFireExpectations")
    public void getRateOfFireShouldReturnPossibleAmountOfShotsWithDelay(long delay, int expectedRateOfFire) {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, delay);
        int rateOfFire = fireMode.getRateOfFire();

        assertEquals(expectedRateOfFire, rateOfFire);
    }

    @Test
    public void shouldNeverBeCycling() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, DELAY_BETWEEN_SHOTS);

        assertFalse(fireMode.isCycling());
    }

    @Test
    public void shouldNotBeCyclingAfterActivation() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(item, taskRunner, DELAY_BETWEEN_SHOTS);
        fireMode.activateCycle();

        assertFalse(fireMode.isCycling());
    }
}
