package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private GunUser user;

    private ItemController<GunUser> controller;

    @BeforeEach
    void setUp() {
        controller = new ItemController<>();
    }

    @Test
    @DisplayName("cancelAllFunctions cancels all functions that are currently performing")
    void cancelAllFunctions() {
        Function<GunUser> function1 = mock();
        when(function1.isPerforming()).thenReturn(true);

        Function<GunUser> function2 = mock();

        ActionBinding<GunUser> binding1 = new ActionBinding<>(function1, 1, false, true, false);
        ActionBinding<GunUser> binding2 = new ActionBinding<>(function2, 2, false, true, false);

        controller.bind(Action.LEFT_CLICK, binding1);
        controller.bind(Action.LEFT_CLICK, binding2);
        controller.cancelAllFunctions();

        verify(function1).cancel();
        verify(function2, never()).cancel();
    }

    @Test
    @DisplayName("performAction triggers only the first function when its action binding stops the chain")
    void performAction_firstFunctionIsSuccessful() {
        Function<GunUser> function1 = mock();
        when(function1.perform(user)).thenReturn(FunctionResult.SUCCESS);

        Function<GunUser> function2 = mock();

        ActionBinding<GunUser> binding1 = new ActionBinding<>(function1, 1, false, true, false);
        ActionBinding<GunUser> binding2 = new ActionBinding<>(function2, 2, false, true, false);

        controller.bind(Action.LEFT_CLICK, binding1);
        controller.bind(Action.LEFT_CLICK, binding2);
        ActionResult result = controller.performActionNew(Action.LEFT_CLICK, user);

        assertThat(result.performed()).isTrue();
        assertThat(result.cancelEvent()).isFalse();

        verify(function1).perform(user);
        verify(function2, never()).perform(user);
    }

    @Test
    @DisplayName("performAction does not perform any function when given action has no configured functions")
    void performAction_actionWithoutFunctions() {
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, true, false);

        controller.bind(Action.LEFT_CLICK, binding);
        ActionResult result = controller.performActionNew(Action.RIGHT_CLICK, user);

        assertThat(result.performed()).isFalse();
        assertThat(result.cancelEvent()).isFalse();

        verify(function, never()).perform(user);
    }

    @Test
    @DisplayName("performAction does not perform any function when another blocking function is performing")
    void performAction_blockingFunctionIsPerforming() {
        Function<GunUser> function1 = mock();
        when(function1.isPerforming()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(function1.perform(user)).thenReturn(FunctionResult.SUCCESS);

        Function<GunUser> function2 = mock();

        ActionBinding<GunUser> binding1 = new ActionBinding<>(function1, 1, true, true, true);
        ActionBinding<GunUser> binding2 = new ActionBinding<>(function2, 2, false, true, false);

        controller.bind(Action.LEFT_CLICK, binding1);
        controller.bind(Action.LEFT_CLICK, binding2);
        ActionResult result1 = controller.performActionNew(Action.LEFT_CLICK, user);
        ActionResult result2 = controller.performActionNew(Action.LEFT_CLICK, user);

        assertThat(result1.performed()).isTrue();
        assertThat(result1.cancelEvent()).isTrue();
        assertThat(result2.performed()).isFalse();
        assertThat(result2.cancelEvent()).isFalse();

        verify(function1, atMost(1)).perform(user);
        verify(function2, never()).perform(user);
    }

    @Test
    @DisplayName("performAction performs function when another function is performing but is not blocking")
    void performAction_nonBlockingFunctionIsPerforming() {
        Function<GunUser> function1 = mock();
        when(function1.isPerforming()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(function1.perform(user)).thenReturn(FunctionResult.SUCCESS);

        Function<GunUser> function2 = mock();
        when(function2.perform(user)).thenReturn(FunctionResult.SUCCESS);

        ActionBinding<GunUser> binding1 = new ActionBinding<>(function1, 1, false, true, false);
        ActionBinding<GunUser> binding2 = new ActionBinding<>(function2, 2, false, true, true);

        controller.bind(Action.LEFT_CLICK, binding1);
        controller.bind(Action.LEFT_CLICK, binding2);
        ActionResult result1 = controller.performActionNew(Action.LEFT_CLICK, user);
        ActionResult result2 = controller.performActionNew(Action.LEFT_CLICK, user);

        assertThat(result1.performed()).isTrue();
        assertThat(result1.cancelEvent()).isFalse();
        assertThat(result2.performed()).isTrue();
        assertThat(result2.cancelEvent()).isTrue();

        verify(function1, atMost(1)).perform(user);
        verify(function2, atMost(1)).perform(user);
    }
}
