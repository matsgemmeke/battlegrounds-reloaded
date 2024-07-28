package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityStorageTest {

    private GamePlayer gamePlayer;
    private Player player;
    private UUID uuid;

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);
    }

    @Test
    public void shouldAddEntityToCollection() {
        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(gamePlayer);

        assertEquals(gamePlayer, storage.getEntity(player));
    }

    @Test
    public void shouldReturnNullIfThereIsNoCorrespondingEntity() {
        GamePlayer otherPlayer = this.createNewGamePlayer();

        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(otherPlayer);

        assertNull(storage.getEntity(player));
    }

    @Test
    public void shouldReturnAllStoredInstances() {
        GamePlayer otherPlayer = this.createNewGamePlayer();

        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(otherPlayer);

        Collection<GamePlayer> players = storage.getEntities();

        assertEquals(1, players.size());
    }

    private GamePlayer createNewGamePlayer() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        return gamePlayer;
    }

    @Test
    public void shouldRemoveEntityFromCollection() {
        EntityStorage<GamePlayer> storage = new EntityStorage<>();
        storage.addEntity(gamePlayer);
        storage.removeEntity(uuid);

        assertNull(storage.getEntity(uuid));
    }
}
