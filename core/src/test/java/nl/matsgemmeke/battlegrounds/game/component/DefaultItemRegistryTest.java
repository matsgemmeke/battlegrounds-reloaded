package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultItemRegistryTest {

    private EntityStorage<GameItem> itemStorage;

    @BeforeEach
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
    public void shouldFindByUUIDAndReturnMatchingEntity() {
        UUID uuid = UUID.randomUUID();

        Item item = mock(Item.class);
        when(item.getUniqueId()).thenReturn(uuid);

        DefaultItemRegistry itemRegistry = new DefaultItemRegistry(itemStorage);
        GameItem gameItem = itemRegistry.registerEntity(item);

        GameItem result = itemRegistry.findByUUID(uuid);

        assertEquals(gameItem, result);
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
    public void shouldReturnRegisteredIfStorageContainsEntryWithGivenUUID() {
        UUID uuid = UUID.randomUUID();

        Item item = mock(Item.class);
        when(item.getUniqueId()).thenReturn(uuid);

        DefaultItemRegistry itemRegistry = new DefaultItemRegistry(itemStorage);
        itemRegistry.registerEntity(item);

        boolean registered = itemRegistry.isRegistered(uuid);

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
