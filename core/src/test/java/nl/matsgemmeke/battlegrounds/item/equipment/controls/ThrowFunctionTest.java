package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.audio.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.mechanism.EquipmentMechanism;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private AudioEmitter audioEmitter;
    private EquipmentMechanism mechanism;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        mechanism = mock(EquipmentMechanism.class);
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

        ThrowFunction function = new ThrowFunction(mechanism, itemStack, audioEmitter, 2.0);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(mechanism).prime();
        verify(world).dropItem(location, itemStack);
    }

    @Test
    public void shouldNotThrowIfItemStackIsNull() {
        World world = mock(World.class);

        Entity entity = mock(Entity.class);
        when(entity.getWorld()).thenReturn(world);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        ThrowFunction function = new ThrowFunction(mechanism, null, audioEmitter, 2.0);
        function.perform(holder);

        verify(mechanism, never()).prime();
        verify(world, never()).dropItem(any(), any());
    }
}
