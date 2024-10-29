package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DelayedActivationTest {

    private ItemEffect effect;
    private ItemHolder holder;
    private long delayUntilActivation;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        effect = mock(ItemEffect.class);
        holder = mock(ItemHolder.class);
        taskRunner = mock(TaskRunner.class);
        delayUntilActivation = 1L;
    }

    @Test
    public void shouldActivateEffectAtAllSourcesWhenActivating() {
        EffectSource source = mock(EffectSource.class);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(mock(BukkitTask.class));

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);
        activation.activateInstantly(holder);

        verify(effect).activate(holder, source);
    }

    @Test
    public void isPrimedReturnsFalseWhenActivationHasNoDeployedObjects() {
        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);

        boolean primed = activation.isPrimed();

        assertFalse(primed);
    }
}
