package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultPlayerRegistryTest {

    private EntityStorage<GamePlayer> playerStorage;
    private InternalsProvider internals;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        playerStorage = (EntityStorage<GamePlayer>) mock(EntityStorage.class);
        internals = mock(InternalsProvider.class);
    }

    @Test
    public void shouldReportAsRegisteredIfStorageContainsRecordWithCorrespondingPlayerEntity() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(playerStorage.getEntity(player)).thenReturn(gamePlayer);

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

        ArgumentCaptor<DefaultGamePlayer> captor = ArgumentCaptor.forClass(DefaultGamePlayer.class);
        verify(playerStorage).addEntity(captor.capture());

        DefaultGamePlayer createdPlayer = captor.getValue();
        assertEquals(player, createdPlayer.getEntity());
    }
}
