package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultPlayerRegistryTest {

    private EntityStorage<GamePlayer> playerStorage;
    private InternalsProvider internals;

    @Before
    public void setUp() {
        playerStorage = new EntityStorage<>();
        internals = mock(InternalsProvider.class);
    }

    @Test
    public void shouldFindByEntityAndReturnMatchingEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerStorage.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(playerStorage, internals);
        GamePlayer result = playerRegistry.findByEntity(player);

        assertEquals(gamePlayer, result);
    }

    @Test
    public void shouldReportAsRegisteredIfStorageContainsRecordWithCorrespondingPlayerEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerStorage.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(playerStorage, internals);
        boolean registered = playerRegistry.isRegistered(player);

        assertTrue(registered);
    }

    @Test
    public void shouldCreateNewInstanceOfGamePlayerAndRegisterToGameStorage() {
        Player player = mock(Player.class);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(playerStorage, internals);
        GamePlayer gamePlayer = playerRegistry.registerEntity(player);

        assertTrue(gamePlayer instanceof DefaultGamePlayer);
    }
}
