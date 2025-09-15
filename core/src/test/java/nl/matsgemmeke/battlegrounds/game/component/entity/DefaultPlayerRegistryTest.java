package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DefaultPlayerRegistryTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID PLAYER_UNIQUE_ID = UUID.randomUUID();

    private DefaultGamePlayerFactory gamePlayerFactory;
    private GameContextProvider gameContextProvider;

    @BeforeEach
    public void setUp() {
        gamePlayerFactory = mock(DefaultGamePlayerFactory.class);
        gameContextProvider = mock(GameContextProvider.class);
    }

    @Test
    public void findByEntityReturnsOptionalWithMatchingEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        playerRegistry.registerEntity(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByEntity(player);

        assertThat(gamePlayerOptional).hasValue(gamePlayer);
    }

    @Test
    public void findByEntityReturnsEmptyOptionalWhereThereIsNoMatch() {
        Player player = mock(Player.class);
        Player otherPlayer = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        playerRegistry.registerEntity(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByEntity(otherPlayer);

        assertThat(gamePlayerOptional).isEmpty();
    }

    @Test
    public void findByUniqueIdReturnsMatchingEntity() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        playerRegistry.registerEntity(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID);

        assertThat(gamePlayerOptional).hasValue(gamePlayer);
    }

    @Test
    public void getAllReturnsPlayersFromStorage() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        playerRegistry.registerEntity(player);
        Collection<GamePlayer> gamePlayers = playerRegistry.getAll();

        assertThat(gamePlayers).containsExactly(gamePlayer);
    }

    @Test
    public void isRegisteredReturnsTrueIfStorageContainsRecordWithCorrespondingPlayerEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        playerRegistry.registerEntity(player);
        boolean registered = playerRegistry.isRegistered(player);

        assertThat(registered).isTrue();
    }

    @Test
    public void isRegisteredReturnsTrueIfStorageContainsEntryWithGivenUUID() {
        UUID uuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        playerRegistry.registerEntity(player);
        boolean registered = playerRegistry.isRegistered(uuid);

        assertThat(registered).isTrue();
    }

    @Test
    public void deregisterRemovesGivenPlayerUuidFromPlayerContainer() {
        UUID playerUuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        playerRegistry.registerEntity(player);
        playerRegistry.deregister(playerUuid);

        assertThat(playerRegistry.findByUniqueId(playerUuid)).isEmpty();
    }

    @Test
    public void registerEntityCreatesNewInstanceOfGamePlayerAndRegisterToGameStorage() {
        UUID playerId = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY);
        GamePlayer createdGamePlayer = playerRegistry.registerEntity(player);

        assertThat(createdGamePlayer).isEqualTo(gamePlayer);

        verify(gameContextProvider).registerEntity(playerId, GAME_KEY);
    }
}
