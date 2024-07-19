package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class DefaultItemRegistryTest {

    private Game game;

    @Before
    public void setUp() {
        game = mock(Game.class);
    }

    @Test
    public void shouldCreateNewInstanceOfItemAndRegisterToGameStorage() {
        EntityStorage<GameItem> itemStorage = (EntityStorage<GameItem>) mock(EntityStorage.class);
        when(game.getItemStorage()).thenReturn(itemStorage);

        Item item = mock(Item.class);

        DefaultItemRegistry itemRegistry = new DefaultItemRegistry(game);
        GameItem gameItem = itemRegistry.registerEntity(item);

        assertTrue(gameItem instanceof DefaultGameItem);

        ArgumentCaptor<DefaultGameItem> captor = ArgumentCaptor.forClass(DefaultGameItem.class);
        verify(itemStorage).addEntity(captor.capture());

        DefaultGameItem createdItem = captor.getValue();
        assertEquals(item, createdItem.getEntity());
    }
}
