package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultPlayerRegistryTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID PLAYER_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private DefaultGamePlayerFactory gamePlayerFactory;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private HitboxProvider<Player> hitboxProvider;
    @Mock
    private HitboxResolver hitboxResolver;

    private DefaultPlayerRegistry playerRegistry;

    @BeforeEach
    void setUp() {
        playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY, hitboxResolver);
    }

    @Test
    void findByUniqueIdReturnsMatchingEntity() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(player, hitboxProvider)).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID);

        assertThat(gamePlayerOptional).hasValue(gamePlayer);
    }

    @Test
    void getAllReturnsPlayersFromStorage() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(player, hitboxProvider)).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        Collection<GamePlayer> gamePlayers = playerRegistry.getAll();

        assertThat(gamePlayers).containsExactly(gamePlayer);
    }

    @Test
    void isRegisteredReturnsTrueIfStorageContainsEntryWithGivenUUID() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(player, hitboxProvider)).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        boolean registered = playerRegistry.isRegistered(PLAYER_UNIQUE_ID);

        assertThat(registered).isTrue();
    }

    @Test
    void deregisterRemovesGivenPlayerUuidFromPlayerContainer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(player, hitboxProvider)).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        playerRegistry.deregister(PLAYER_UNIQUE_ID);

        assertThat(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).isEmpty();
    }

    @Test
    void registerEntityCreatesNewInstanceOfGamePlayerAndRegisterToGameStorage() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(player, hitboxProvider)).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        GamePlayer createdGamePlayer = playerRegistry.register(player);

        assertThat(createdGamePlayer).isEqualTo(gamePlayer);

        verify(gameContextProvider).registerEntity(PLAYER_UNIQUE_ID, GAME_KEY);
    }
}
