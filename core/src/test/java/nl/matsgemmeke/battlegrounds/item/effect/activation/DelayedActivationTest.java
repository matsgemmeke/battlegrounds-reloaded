package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DelayedActivationTest {

    private static final long DELAY_UNTIL_ACTIVATION = 1L;

    private ItemEffectSource source;
    private ItemHolder holder;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        source = mock(ItemEffectSource.class);
        holder = mock(ItemHolder.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void cancelDoesNotCancelTaskIfNotPrimed() {
        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.cancel();

        assertFalse(activation.isPrimed());
    }

    @Test
    public void cancelCancelsTaskIfPrimed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION))).thenReturn(task);

        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.prime(context, () -> {});
        activation.cancel();

        assertFalse(activation.isPrimed());

        verify(task).cancel();
    }

    @Test
    public void primeDoesNotScheduleDelayedActivationTwiceIfAlreadyPrimed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);

        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.prime(context, () -> {});
        activation.prime(context, () -> {});

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION));
    }

    @Test
    public void primeFinishesActivationAfterDelayAndRemovesItemIfSourceIsDeployed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);
        Procedure onActivate = mock(Procedure.class);

        when(source.isDeployed()).thenReturn(true);

        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.prime(context, onActivate);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(DELAY_UNTIL_ACTIVATION));

        runnableCaptor.getValue().run();

        verify(holder).setHeldItem(null);
        verify(onActivate).apply();
    }

    @Test
    public void primeFinishesActivationAfterDelayAndDoesNotRemoveItemIfSourceIsNotDeployed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);
        Procedure onActivate = mock(Procedure.class);

        when(source.isDeployed()).thenReturn(false);

        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.prime(context, onActivate);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(DELAY_UNTIL_ACTIVATION));

        runnableCaptor.getValue().run();

        verify(holder, never()).setHeldItem(null);
        verify(onActivate).apply();
    }
}
