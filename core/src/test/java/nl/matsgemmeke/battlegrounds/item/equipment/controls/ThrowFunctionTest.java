package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.DroppedItem;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private AudioEmitter audioEmitter;
    private double projectileSpeed;
    private ItemEffectActivation effectActivation;
    private ItemTemplate itemTemplate;
    private long delayAfterThrow;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        projectileSpeed = 2.0;
        effectActivation = mock(ItemEffectActivation.class);
        itemTemplate = mock(ItemTemplate.class);
        delayAfterThrow = 1L;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldNotBePerformingIfNoThrowsWereExecuted() {
        ThrowFunction function = new ThrowFunction(itemTemplate, effectActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void shouldBePerformingIfThrowsWereRecentlyExecuted() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        World world = mock(World.class);
        when(world.dropItem(any(Location.class), eq(itemStack))).thenReturn(mock(Item.class));

        Location throwingDirection = new Location(world, 1.0, 1.0, 1.0);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getThrowingDirection()).thenReturn(throwingDirection);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(itemTemplate, effectActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        function.perform(holder);

        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldPrimeDroppedItemWhenPerforming() {
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Item itemEntity = mock(Item.class);
        when(itemEntity.getLocation()).thenReturn(location);

        ItemStack itemStack = new ItemStack(Material.SHEARS);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        World world = mock(World.class);
        when(world.dropItem(location, itemStack)).thenReturn(itemEntity);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getThrowingDirection()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(itemTemplate, effectActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<DroppedItem> captor = ArgumentCaptor.forClass(DroppedItem.class);
        verify(effectActivation).prime(eq(holder), captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayAfterThrow));
        verify(world).dropItem(location, itemStack);
    }
}
