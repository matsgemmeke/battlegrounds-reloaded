package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultItemRegistryTest {

    private EntityStorage<GameItem> itemStorage;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        itemStorage = (EntityStorage<GameItem>) mock(EntityStorage.class);
    }

    @Test
    public void shouldReportAsRegisteredIfStorageContainsRecordWithCorrespondingItemEntity() {
        GameItem gameItem = mock(GameItem.class);
        Item item = mock(Item.class);

        when(itemStorage.getEntity(item)).thenReturn(gameItem);

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

        ArgumentCaptor<DefaultGameItem> captor = ArgumentCaptor.forClass(DefaultGameItem.class);
        verify(itemStorage).addEntity(captor.capture());

        DefaultGameItem createdItem = captor.getValue();
        assertEquals(item, createdItem.getEntity());
    }
}
