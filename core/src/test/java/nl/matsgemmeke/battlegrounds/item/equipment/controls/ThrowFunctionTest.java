package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.DroppedItem;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private AudioEmitter audioEmitter;
    private Deployable item;
    private double projectileSpeed;
    private ItemMechanismActivation mechanismActivation;
    private ItemStack itemStack;
    private long delayAfterThrow;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        item = mock(Deployable.class);
        projectileSpeed = 2.0;
        mechanismActivation = mock(ItemMechanismActivation.class);
        itemStack = new ItemStack(Material.SHEARS);
        delayAfterThrow = 1L;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldDeployItemWhenPerforming() {
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Item itemEntity = mock(Item.class);
        when(itemEntity.getLocation()).thenReturn(location);

        World world = mock(World.class);
        when(world.dropItem(location, itemStack)).thenReturn(itemEntity);

        when(item.isDeployed()).thenReturn(false);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getThrowingDirection()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);
        when(holder.isAbleToThrow()).thenReturn(true);

        ThrowFunction function = new ThrowFunction(item, itemStack, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<DroppedItem> captor = ArgumentCaptor.forClass(DroppedItem.class);
        verify(item).onDeploy(captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(holder).setAbleToThrow(false);
        verify(mechanismActivation).prime(holder);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayAfterThrow));
        verify(world).dropItem(location, itemStack);
    }

    @Test
    public void shouldNotThrowIfItemAlreadyIsDeployed() {
        when(item.isDeployed()).thenReturn(true);

        World world = mock(World.class);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(item, itemStack, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        function.perform(holder);

        verifyNoInteractions(audioEmitter);
        verifyNoInteractions(mechanismActivation);
        verifyNoInteractions(world);
    }

    @Test
    public void shouldNotThrowIfHolderIsUnableToThrow() {
        when(item.isDeployed()).thenReturn(false);

        World world = mock(World.class);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getWorld()).thenReturn(world);
        when(holder.isAbleToThrow()).thenReturn(false);

        ThrowFunction function = new ThrowFunction(item, itemStack, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        function.perform(holder);

        verifyNoInteractions(audioEmitter);
        verifyNoInteractions(mechanismActivation);
        verifyNoInteractions(world);
    }
}
