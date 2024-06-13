package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.activation.EquipmentActivation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private AudioEmitter audioEmitter;
    private double projectileSpeed;
    private EquipmentActivation activation;
    private long delayBetweenThrows;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        projectileSpeed = 2.0;
        activation = mock(EquipmentActivation.class);
        delayBetweenThrows = 1;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldReturnAvailabilityBasedOnThrowingDelay() {
        Item item = mock(Item.class);

        World world = mock(World.class);
        when(world.dropItem(any(), any())).thenReturn(item);

        ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
        Location location = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Entity entity = mock(Entity.class);
        when(entity.getWorld()).thenReturn(world);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getThrowingDirection()).thenReturn(location);

        ThrowFunction function = new ThrowFunction(activation, itemStack, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        function.perform(holder);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldReturnPerformingStateBasedOnThrowingDelay() {
        Item item = mock(Item.class);

        World world = mock(World.class);
        when(world.dropItem(any(), any())).thenReturn(item);

        ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
        Location location = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Entity entity = mock(Entity.class);
        when(entity.getWorld()).thenReturn(world);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getThrowingDirection()).thenReturn(location);

        ThrowFunction function = new ThrowFunction(activation, itemStack, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        function.perform(holder);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldThrowItemWhenPerforming() {
        Item item = mock(Item.class);

        World world = mock(World.class);
        when(world.dropItem(any(), any())).thenReturn(item);

        ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
        Location location = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Entity entity = mock(Entity.class);
        when(entity.getWorld()).thenReturn(world);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getThrowingDirection()).thenReturn(location);

        ThrowFunction function = new ThrowFunction(activation, itemStack, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(activation).prime();
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayBetweenThrows));
        verify(world).dropItem(location, itemStack);
    }

    @Test
    public void shouldNotThrowIfItemStackIsNull() {
        World world = mock(World.class);

        Entity entity = mock(Entity.class);
        when(entity.getWorld()).thenReturn(world);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        ThrowFunction function = new ThrowFunction(activation, null, audioEmitter, taskRunner, projectileSpeed, delayBetweenThrows);
        function.perform(holder);

        verify(activation, never()).prime();
        verify(world, never()).dropItem(any(), any());
    }
}
