package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private static final double VELOCITY = 2.0;
    private static final Iterable<GameSound> THROW_SOUNDS = Collections.emptySet();
    private static final long DELAY_AFTER_THROW = 1L;

    private AudioEmitter audioEmitter;
    private ItemEffectActivation effectActivation;
    private ItemTemplate itemTemplate;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        effectActivation = mock(ItemEffectActivation.class);
        itemTemplate = mock(ItemTemplate.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldNotBePerformingIfNoThrowsWereExecuted() {
        ThrowProperties properties = new ThrowProperties(THROW_SOUNDS, VELOCITY, DELAY_AFTER_THROW);

        ThrowFunction function = new ThrowFunction(properties, itemTemplate, effectActivation, audioEmitter, taskRunner);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void shouldBePerformingIfThrowsWereRecentlyExecuted() {
        ThrowProperties properties = new ThrowProperties(THROW_SOUNDS, VELOCITY, DELAY_AFTER_THROW);

        ItemStack itemStack = new ItemStack(Material.SHEARS);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        World world = mock(World.class);
        when(world.dropItem(any(Location.class), eq(itemStack))).thenReturn(mock(Item.class));

        Location throwingDirection = new Location(world, 1.0, 1.0, 1.0);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getThrowingDirection()).thenReturn(throwingDirection);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(properties, itemTemplate, effectActivation, audioEmitter, taskRunner);
        function.perform(holder);

        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldPrimeDroppedItemWhenPerforming() {
        ThrowProperties properties = new ThrowProperties(THROW_SOUNDS, VELOCITY, DELAY_AFTER_THROW);
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

        ThrowFunction function = new ThrowFunction(properties, itemTemplate, effectActivation, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<DroppedItem> captor = ArgumentCaptor.forClass(DroppedItem.class);
        verify(effectActivation).prime(eq(holder), captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_AFTER_THROW));
        verify(world).dropItem(location, itemStack);
    }
}
