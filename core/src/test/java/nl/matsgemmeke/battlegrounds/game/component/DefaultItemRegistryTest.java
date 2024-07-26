package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultItemRegistryTest {

    private EntityStorage<GameItem> itemStorage;

    @Before
    public void setUp() {
        itemStorage = new EntityStorage<>();
    }

    @Test
    public void shouldFindByEntityAndReturnMatchingEntity() {
        Item item = mock(Item.class);

        GameItem gameItem = mock(GameItem.class);
        when(gameItem.getEntity()).thenReturn(item);

        itemStorage.addEntity(gameItem);

        DefaultItemRegistry itemRegistry = new DefaultItemRegistry(itemStorage);
        GameItem result = itemRegistry.findByEntity(item);

        assertEquals(gameItem, result);
    }

    @Test
    public void shouldReturnNoEntityIfThereIsNoMatch() {
        Item item = mock(Item.class);
        Item otherItem = mock(Item.class);

        GameItem gameItem = mock(GameItem.class);
        when(gameItem.getEntity()).thenReturn(item);

        itemStorage.addEntity(gameItem);

        DefaultItemRegistry itemRegistry = new DefaultItemRegistry(itemStorage);
        GameItem result = itemRegistry.findByEntity(otherItem);

        assertNull(result);
    }

    @Test
    public void shouldReportAsRegisteredIfStorageContainsRecordWithCorrespondingItemEntity() {
        Item item = mock(Item.class);

        GameItem gameItem = mock(GameItem.class);
        when(gameItem.getEntity()).thenReturn(item);

        itemStorage.addEntity(gameItem);

        DefaultItemRegistry itemRegistry = new DefaultItemRegistry(itemStorage);
        boolean registered = itemRegistry.isRegistered(item);

        assertTrue(registered);
    }

    @Test
    public void shouldCreateNewInstanceOfGameItemAndRegisterToGameStorage() {
        Item item = mock(Item.class);

        DefaultItemRegistry itemRegistry = new DefaultItemRegistry(itemStorage);
        GameItem gameItem = itemRegistry.registerEntity(item);

        assertTrue(gameItem instanceof DefaultGameItem);
    }
}
