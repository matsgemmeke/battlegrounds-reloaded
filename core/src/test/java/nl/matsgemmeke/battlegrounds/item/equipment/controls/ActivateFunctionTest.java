package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActivateFunctionTest {

    private static final Iterable<GameSound> ACTIVATION_SOUNDS = Collections.emptySet();
    private static final long DELAY_UNTIL_ACTIVATION = 1L;

    private ActivateProperties properties;
    private AudioEmitter audioEmitter;
    private Equipment equipment;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        properties = new ActivateProperties(ACTIVATION_SOUNDS, DELAY_UNTIL_ACTIVATION);
        audioEmitter = mock(AudioEmitter.class);
        equipment = mock(Equipment.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void isAvailableReturnsFalseIfEquipmentHasNoActivator() {
        when(equipment.getActivator()).thenReturn(null);

        ActivateFunction function = new ActivateFunction(properties, equipment, audioEmitter, taskRunner);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isAvailableReturnsFalseIfEquipmentActivatorIsNotReady() {
        Activator activator = mock(Activator.class);
        when(activator.isReady()).thenReturn(false);

        when(equipment.getActivator()).thenReturn(activator);

        ActivateFunction function = new ActivateFunction(properties, equipment, audioEmitter, taskRunner);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isAvailableReturnsTrueIfEquipmentActivatorIsReady() {
        Activator activator = mock(Activator.class);
        when(activator.isReady()).thenReturn(true);

        when(equipment.getActivator()).thenReturn(activator);

        ActivateFunction function = new ActivateFunction(properties, equipment, audioEmitter, taskRunner);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void performThrowsExceptionIfEquipmentHasNoEffectActivation() {
        when(equipment.getEffectActivation()).thenReturn(null);

        EquipmentHolder holder = mock(EquipmentHolder.class);

        ActivateFunction function = new ActivateFunction(properties, equipment, audioEmitter, taskRunner);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
    }

    @Test
    public void performReturnsTrueAndRemovesHeldItemAndPerformDelayedTaskForActivatingEffectActivation() {
        Location location = new Location(null, 1, 1, 1);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION))).then(answer -> {
            answer.getArgument(0, Runnable.class).run();
            return null;
        });

        ActivateFunction function = new ActivateFunction(properties, equipment, audioEmitter, taskRunner);
        function.perform(holder);

        verify(holder).setHeldItem(null);
        verify(effectActivation).activateInstantly(holder);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION));
    }
}
