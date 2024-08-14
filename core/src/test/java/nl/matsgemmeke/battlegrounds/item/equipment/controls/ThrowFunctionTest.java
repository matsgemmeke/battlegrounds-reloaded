package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private AudioEmitter audioEmitter;
    private double projectileSpeed;
    private Droppable item;
    private ItemMechanismActivation mechanismActivation;
    private long delayBetweenThrows;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        projectileSpeed = 2.0;
        item = mock(Droppable.class);
        mechanismActivation = mock(ItemMechanismActivation.class);
        delayBetweenThrows = 1;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldReturnAvailabilityBasedOnThrowingDelay() {
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Item droppedItem = mock(Item.class);

        when(item.canDrop()).thenReturn(true);
        when(item.dropItem(location)).thenReturn(droppedItem);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getThrowingDirection()).thenReturn(location);

        ThrowFunction function = new ThrowFunction(item, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        function.perform(holder);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldReturnPerformingStateBasedOnThrowingDelay() {
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);

        Item droppedItem = mock(Item.class);

        when(item.canDrop()).thenReturn(true);
        when(item.dropItem(location)).thenReturn(droppedItem);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getThrowingDirection()).thenReturn(location);

        ThrowFunction function = new ThrowFunction(item, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        function.perform(holder);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldThrowItemWhenPerforming() {
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);

        Item droppedItem = mock(Item.class);

        when(item.canDrop()).thenReturn(true);
        when(item.dropItem(location)).thenReturn(droppedItem);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getThrowingDirection()).thenReturn(location);

        ThrowFunction function = new ThrowFunction(item, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(item).dropItem(location);
        verify(mechanismActivation).prime(holder);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayBetweenThrows));
    }

    @Test
    public void shouldNotThrowIfItemCannotBeThrown() {
        when(item.canDrop()).thenReturn(false);

        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        EquipmentHolder holder = mock(EquipmentHolder.class);

        ThrowFunction function = new ThrowFunction(item, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        function.perform(holder);

        verify(mechanismActivation, never()).prime(holder);
        verify(item, never()).dropItem(location);
    }
}
