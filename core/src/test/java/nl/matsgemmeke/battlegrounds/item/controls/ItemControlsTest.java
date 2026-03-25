package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ItemControlsTest {

    @Test
    public void shouldCancelPerformingFunctionsWhenCancelling() {
        ItemFunction<GunUser> function1 = mock();
        when(function1.isPerforming()).thenReturn(true);

        ItemFunction<GunUser> function2 = mock();

        ItemControls<GunUser> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.cancelAllFunctions();

        verify(function1).cancel();
        verify(function2, never()).cancel();
    }

    @Test
    public void shouldTriggerCorrespondingFunctionWhenPerformingAction() {
        GunUser user = mock(GunUser.class);

        ItemFunction<GunUser> function = mock();
        when(function.isAvailable()).thenReturn(true);

        ItemControls<GunUser> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function).perform(user);
    }

    @Test
    public void shouldOnlyTriggerTheFirstFunctionWhenPerformingAction() {
        GunUser user = mock(GunUser.class);

        ItemFunction<GunUser> function1 = mock();
        when(function1.isAvailable()).thenReturn(true);
        when(function1.perform(user)).thenReturn(true);

        ItemFunction<GunUser> function2 = mock();
        when(function2.isAvailable()).thenReturn(true);

        ItemControls<GunUser> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function1).perform(user);
        verify(function2, never()).perform(user);
    }

    @Test
    public void shouldNotTriggerFunctionIfActionDoesNotCorrespond() {
        GunUser user = mock(GunUser.class);
        ItemFunction<GunUser> function = mock();

        ItemControls<GunUser> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function);
        controls.performAction(Action.RIGHT_CLICK, user);

        verify(function, never()).perform(user);
    }

    @Test
    public void shouldNotTriggerFunctionIsIfNotAvailable() {
        GunUser user = mock(GunUser.class);
        ItemFunction<GunUser> function = mock();

        ItemControls<GunUser> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function, never()).perform(user);
    }

    @Test
    public void shouldNotTriggerFunctionIfAnotherBlockingFunctionIsPerforming() {
        GunUser user = mock(GunUser.class);

        ItemFunction<GunUser> function1 = mock();
        when(function1.isAvailable()).thenReturn(true).thenReturn(false);
        when(function1.isBlocking()).thenReturn(true);
        when(function1.isPerforming()).thenReturn(false).thenReturn(true);
        when(function1.perform(user)).thenReturn(true);

        ItemFunction<GunUser> function2 = mock();
        when(function2.isAvailable()).thenReturn(true);

        ItemControls<GunUser> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, user);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function1).perform(user);
        verify(function2, never()).perform(user);
    }

    @Test
    public void shouldTriggerFunctionIfAnotherFunctionIsPerformingButNotBlocking() {
        GunUser user = mock(GunUser.class);

        ItemFunction<GunUser> function1 = mock();
        when(function1.isAvailable()).thenReturn(true).thenReturn(false);
        when(function1.isPerforming()).thenReturn(false).thenReturn(true);
        when(function1.perform(user)).thenReturn(true);

        ItemFunction<GunUser> function2 = mock();
        when(function2.isAvailable()).thenReturn(true);

        ItemControls<GunUser> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, user);
        controls.performAction(Action.LEFT_CLICK, user);

        verify(function1).perform(user);
        verify(function2).perform(user);
    }
}
