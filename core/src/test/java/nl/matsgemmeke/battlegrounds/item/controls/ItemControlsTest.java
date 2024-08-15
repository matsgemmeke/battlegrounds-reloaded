package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.Test;

import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ItemControlsTest {

    @Test
    public void shouldCancelPerformingFunctionsWhenCancelling() {
        ItemFunction<GunHolder> function1 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function1.isPerforming()).thenReturn(true);

        ItemFunction<GunHolder> function2 = (ItemFunction<GunHolder>) mock(ItemFunction.class);

        ItemControls<GunHolder> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.cancelAllFunctions();

        verify(function1).cancel();
        verify(function2, never()).cancel();
    }

    @Test
    public void shouldTriggerCorrespondingFunctionWhenPerformingAction() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        ItemControls<GunHolder> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function);
        controls.performAction(Action.LEFT_CLICK, holder);

        verify(function).perform(holder);
    }

    @Test
    public void shouldOnlyTriggerTheFirstFunctionWhenPerformingAction() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function1 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function1.isAvailable()).thenReturn(true);
        when(function1.perform(holder)).thenReturn(true);

        ItemFunction<GunHolder> function2 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function2.isAvailable()).thenReturn(true);

        ItemControls<GunHolder> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, holder);

        verify(function1).perform(holder);
        verify(function2, never()).perform(holder);
    }

    @Test
    public void shouldNotTriggerFunctionIfActionDoesNotCorrespond() {
        GunHolder holder = mock(GunHolder.class);
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);

        ItemControls<GunHolder> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function);
        controls.performAction(Action.RIGHT_CLICK, holder);

        verify(function, never()).perform(holder);
    }

    @Test
    public void shouldNotTriggerFunctionIsIfNotAvailable() {
        GunHolder holder = mock(GunHolder.class);
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);

        ItemControls<GunHolder> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function);
        controls.performAction(Action.LEFT_CLICK, holder);

        verify(function, never()).perform(holder);
    }

    @Test
    public void shouldNotTriggerFunctionIfAnotherBlockingFunctionIsPerforming() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function1 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function1.isAvailable()).thenReturn(true).thenReturn(false);
        when(function1.isBlocking()).thenReturn(true);
        when(function1.isPerforming()).thenReturn(false).thenReturn(true);
        when(function1.perform(holder)).thenReturn(true);

        ItemFunction<GunHolder> function2 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function2.isAvailable()).thenReturn(true);

        ItemControls<GunHolder> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, holder);
        controls.performAction(Action.LEFT_CLICK, holder);

        verify(function1).perform(holder);
        verify(function2, never()).perform(holder);
    }

    @Test
    public void shouldTriggerFunctionIfAnotherFunctionIsPerformingButNotBlocking() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function1 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function1.isAvailable()).thenReturn(true).thenReturn(false);
        when(function1.isPerforming()).thenReturn(false).thenReturn(true);
        when(function1.perform(holder)).thenReturn(true);

        ItemFunction<GunHolder> function2 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function2.isAvailable()).thenReturn(true);

        ItemControls<GunHolder> controls = new ItemControls<>();
        controls.addControl(Action.LEFT_CLICK, function1);
        controls.addControl(Action.LEFT_CLICK, function2);
        controls.performAction(Action.LEFT_CLICK, holder);
        controls.performAction(Action.LEFT_CLICK, holder);

        verify(function1).perform(holder);
        verify(function2).perform(holder);
    }
}
