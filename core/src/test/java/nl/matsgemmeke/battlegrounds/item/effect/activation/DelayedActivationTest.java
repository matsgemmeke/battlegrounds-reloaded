package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DelayedActivationTest {

    private static final long DELAY_UNTIL_ACTIVATION = 1L;

    private Deployer deployer;
    private Entity entity;
    private ItemEffectContext context;
    private ItemEffectSource source;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        taskRunner = mock(TaskRunner.class);
        context = new ItemEffectContext(deployer, entity, source);
    }

    @Test
    public void cancelDoesNotCancelTaskIfNotPrimed() {
        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.cancel();

        assertFalse(activation.isPrimed());
    }

    @Test
    public void cancelCancelsTaskIfPrimed() {
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
        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.prime(context, () -> {});
        activation.prime(context, () -> {});

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION));
    }

    @Test
    public void primeFinishesActivationAfterDelayAndRemovesItemIfSourceIsDeployed() {
        Procedure onActivate = mock(Procedure.class);

        when(source.isDeployed()).thenReturn(true);

        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.prime(context, onActivate);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(DELAY_UNTIL_ACTIVATION));

        runnableCaptor.getValue().run();

        verify(deployer).setHeldItem(null);
        verify(onActivate).apply();
    }

    @Test
    public void primeFinishesActivationAfterDelayAndDoesNotRemoveItemIfSourceIsNotDeployed() {
        Procedure onActivate = mock(Procedure.class);

        when(source.isDeployed()).thenReturn(false);

        DelayedActivation activation = new DelayedActivation(taskRunner, DELAY_UNTIL_ACTIVATION);
        activation.prime(context, onActivate);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(DELAY_UNTIL_ACTIVATION));

        runnableCaptor.getValue().run();

        verify(deployer, never()).setHeldItem(null);
        verify(onActivate).apply();
    }
}
