package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DelayedActivationTest {

    private static final long DELAY_UNTIL_ACTIVATION = 1L;

    private EffectSource source;
    private ItemHolder holder;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        source = mock(EffectSource.class);
        holder = mock(ItemHolder.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activateFinishesActivationAfterDelayAndRemovesItemIfSourceIsDeployed() {
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
    public void activateFinishesActivationAfterDelayAndDoesNotRemoveItemIfSourceIsNotDeployed() {
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
