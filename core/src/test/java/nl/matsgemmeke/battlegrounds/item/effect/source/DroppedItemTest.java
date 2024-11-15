package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DroppedItemTest {

    private Item itemEntity;

    @BeforeEach
    public void setUp() {
        itemEntity = mock(Item.class);
    }

    @Test
    public void shouldExistIfItemEntityIsIntact() {
        when(itemEntity.isDead()).thenReturn(false);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean exists = droppedItem.exists();

        assertTrue(exists);
    }

    @Test
    public void shouldNotExistIfItemEntityDoesNotExist() {
        when(itemEntity.isDead()).thenReturn(true);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean exists = droppedItem.exists();

        assertFalse(exists);
    }

    @Test
    public void returnSameLocationAsItemEntity() {
        Location itemLocation = new Location(null, 1, 1, 1);
        when(itemEntity.getLocation()).thenReturn(itemLocation);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        Location objectLocation = droppedItem.getLocation();

        assertEquals(itemLocation, objectLocation);
    }

    @Test
    public void returnSameWorldAsItemEntity() {
        World itemWorld = mock(World.class);
        when(itemEntity.getWorld()).thenReturn(itemWorld);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        World objectWorld = droppedItem.getWorld();

        assertEquals(itemWorld, objectWorld);
    }

    @Test
    public void isDeployedAlwaysReturnsTrue() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean deployed = droppedItem.isDeployed();

        assertTrue(deployed);
    }

    @Test
    public void removeItemEntityWhenRemovingObject() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.remove();

        verify(itemEntity).remove();
    }
}
