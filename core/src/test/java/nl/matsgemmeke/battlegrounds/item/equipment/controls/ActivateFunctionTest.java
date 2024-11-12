package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ActivateFunctionTest {

    private AudioEmitter audioEmitter;
    private ItemEffectActivation effectActivation;
    private long delayUntilActivation;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        effectActivation = mock(ItemEffectActivation.class);
        delayUntilActivation = 1L;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldRemoveHeldItemAndPerformDelayedTaskThatActivatesEffectActivationWhenPerforming() {
        Location location = new Location(null, 1, 1, 1);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).then(answer -> {
            answer.getArgument(0, Runnable.class).run();
            return null;
        });

        ActivateFunction function = new ActivateFunction(effectActivation, audioEmitter, taskRunner, delayUntilActivation);
        function.perform(holder);

        verify(holder).setHeldItem(null);
        verify(effectActivation).activateInstantly(holder);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayUntilActivation));
    }
}
