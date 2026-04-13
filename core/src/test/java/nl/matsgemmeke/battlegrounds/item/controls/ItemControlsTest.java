package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControlsTest {

    @Mock
    private GunUser user;

    private ItemControls<GunUser> controls;

    @BeforeEach
    void setUp() {
        controls = new ItemControls<>();
    }

    @Test
    @DisplayName("cancelAllFunctions cancels all functions that are currently performing")
    void cancelAllFunctions() {
        Function<GunUser> function1 = mock();
        when(function1.isPerforming()).thenReturn(true);

        Function<GunUser> function2 = mock();

        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.cancelAllFunctions();

        verify(function1).cancel();
        verify(function2, never()).cancel();
    }

    @Test
    @DisplayName("performAction triggers only the first function when its result is successful")
    void performAction_firstFunctionIsSuccessful() {
        Function<GunUser> function1 = mock();
        when(function1.perform(user)).thenReturn(FunctionResult.SUCCESS);

        Function<GunUser> function2 = mock();

        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function1).perform(user);
        verify(function2, never()).perform(user);
    }

    @Test
    @DisplayName("performAction does not perform any function when given action has no configured functions")
    void performAction_actionWithoutFunctions() {
        Function<GunUser> function = mock();

        controls.addControl(Action.LEFT_CLICK, function);
        controls.performAction(Action.RIGHT_CLICK, user);

        verify(function, never()).perform(user);
    }

    @Test
    @DisplayName("performAction does not perform any function when another blocking function is performing")
    void performAction_blockingFunctionIsPerforming() {
        Function<GunUser> function1 = mock();
        when(function1.isBlocking()).thenReturn(true);
        when(function1.isPerforming()).thenReturn(false).thenReturn(true);
        when(function1.perform(user)).thenReturn(FunctionResult.SUCCESS);

        Function<GunUser> function2 = mock();

        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, user);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function1).perform(user);
        verify(function2, never()).perform(user);
    }

    @Test
    @DisplayName("performAction performs function when another function is performing but is not blocking")
    void performAction_nonBlockingFunctionIsPerforming() {
        Function<GunUser> function1 = mock();
        when(function1.isBlocking()).thenReturn(false);
        when(function1.isPerforming()).thenReturn(false).thenReturn(true);
        when(function1.perform(user)).thenReturn(FunctionResult.SUCCESS);

        Function<GunUser> function2 = mock();
        when(function2.perform(user)).thenReturn(FunctionResult.SUCCESS);

        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, user);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function1, atMost(1)).perform(user);
        verify(function2, atMost(1)).perform(user);
    }
}
