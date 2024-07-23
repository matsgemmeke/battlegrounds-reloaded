package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityStorageTest {

    private GamePlayer gamePlayer;
    private Player player;

    @Before
    public void setUp() {
        player = mock(Player.class);

        gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);
    }

    @Test
    public void shouldAddEntityToRegister() {
        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(gamePlayer);

        assertEquals(gamePlayer, storage.getEntity(player));
    }

    @Test
    public void shouldReturnNullIfThereIsNoCorrespondingEntity() {
        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(mock(GamePlayer.class));

        assertNull(storage.getEntity(player));
    }

    @Test
    public void shouldReturnAllStoredInstances() {
        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(mock(GamePlayer.class));

        Set<GamePlayer> players = storage.getEntities();

        assertEquals(1, players.size());
    }

    @Test
    public void shouldRemoveEntityFromRegister() {
        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(gamePlayer);
        storage.removeEntity(gamePlayer);

        assertNull(storage.getEntity(player));
    }
}
