package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployableSource;
import nl.matsgemmeke.battlegrounds.item.deployment.DroppedItem;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private AudioEmitter audioEmitter;
    private DeployableSource item;
    private double projectileSpeed;
    private ItemMechanismActivation mechanismActivation;
    private ItemTemplate itemTemplate;
    private long delayAfterThrow;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        item = mock(DeployableSource.class);
        projectileSpeed = 2.0;
        mechanismActivation = mock(ItemMechanismActivation.class);
        itemTemplate = mock(ItemTemplate.class);
        delayAfterThrow = 1L;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldNotBePerformingIfNoThrowsWereExecuted() {
        ThrowFunction function = new ThrowFunction(item, itemTemplate, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
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

        ThrowFunction function = new ThrowFunction(item, itemTemplate, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        function.perform(holder);

        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldDeployDroppedItemAndPrimeWhenPerforming() {
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Item itemEntity = mock(Item.class);
        when(itemEntity.getLocation()).thenReturn(location);

        ItemStack itemStack = new ItemStack(Material.SHEARS);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        World world = mock(World.class);
        when(world.dropItem(location, itemStack)).thenReturn(itemEntity);

        when(item.getDeployedObjects()).thenReturn(Collections.emptyList());
        when(mechanismActivation.isPrimed()).thenReturn(false);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getThrowingDirection()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(item, itemTemplate, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<DroppedItem> captor = ArgumentCaptor.forClass(DroppedItem.class);
        verify(item).onDeploy(captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(mechanismActivation).primeDeployedObject(holder, captor.getValue());
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayAfterThrow));
        verify(world).dropItem(location, itemStack);
    }

    @Test
    public void shouldDeployDroppedItemAndNotifyMechanismActivationOfDeferredDeployment() {
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

        when(item.getDeployedObjects()).thenReturn(Collections.emptyList());
        when(mechanismActivation.isPrimed()).thenReturn(true);

        ThrowFunction function = new ThrowFunction(item, itemTemplate, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<DroppedItem> captor = ArgumentCaptor.forClass(DroppedItem.class);
        verify(item).onDeploy(captor.capture());

        verify(mechanismActivation).deploy(captor.getValue());
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayAfterThrow));
        verify(world).dropItem(location, itemStack);
    }

    @Test
    public void shouldNotThrowIfItemAlreadyHasDeployedObject() {
        when(item.getDeployedObjects()).thenReturn(List.of(mock(Deployable.class)));

        World world = mock(World.class);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(item, itemTemplate, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        function.perform(holder);

        verifyNoInteractions(audioEmitter);
        verifyNoInteractions(mechanismActivation);
        verifyNoInteractions(world);
    }
}
