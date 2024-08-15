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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private AudioEmitter audioEmitter;
    private double projectileSpeed;
    private Droppable item;
    private ItemMechanismActivation mechanismActivation;
    private long delayAfterThrow;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        projectileSpeed = 2.0;
        item = mock(Droppable.class);
        mechanismActivation = mock(ItemMechanismActivation.class);
        delayAfterThrow = 1L;
        taskRunner = mock(TaskRunner.class);
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
        when(holder.isAbleToThrow()).thenReturn(true);

        ThrowFunction function = new ThrowFunction(item, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(holder).setAbleToThrow(false);
        verify(item).dropItem(location);
        verify(mechanismActivation).prime(holder);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayAfterThrow));
    }

    @Test
    public void shouldNotThrowIfItemCannotBeThrown() {
        when(item.canDrop()).thenReturn(false);

        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        EquipmentHolder holder = mock(EquipmentHolder.class);

        ThrowFunction function = new ThrowFunction(item, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        function.perform(holder);

        verify(mechanismActivation, never()).prime(holder);
        verify(item, never()).dropItem(location);
    }

    @Test
    public void shouldNotThrowIfHolderIsUnableToThrow() {
        when(item.canDrop()).thenReturn(true);

        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.isAbleToThrow()).thenReturn(false);

        ThrowFunction function = new ThrowFunction(item, mechanismActivation, audioEmitter, taskRunner, projectileSpeed, delayAfterThrow);
        function.perform(holder);

        verify(mechanismActivation, never()).prime(holder);
        verify(item, never()).dropItem(location);
    }
}
