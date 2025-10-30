package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
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
    private HitboxResolver hitboxResolver;

    private DefaultPlayerRegistry playerRegistry;

    @BeforeEach
    void setUp() {
        playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, gameContextProvider, GAME_KEY, hitboxResolver);
    }

    @Test
    void findByEntityReturnsOptionalWithMatchingEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        playerRegistry.registerEntity(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByEntity(player);

        assertThat(gamePlayerOptional).hasValue(gamePlayer);
    }

    @Test
    void findByEntityReturnsEmptyOptionalWhereThereIsNoMatch() {
        Player player = mock(Player.class);
        Player otherPlayer = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        playerRegistry.registerEntity(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByEntity(otherPlayer);

        assertThat(gamePlayerOptional).isEmpty();
    }

    @Test
    void findByUniqueIdReturnsMatchingEntity() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        playerRegistry.registerEntity(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID);

        assertThat(gamePlayerOptional).hasValue(gamePlayer);
    }

    @Test
    void getAllReturnsPlayersFromStorage() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        playerRegistry.registerEntity(player);
        Collection<GamePlayer> gamePlayers = playerRegistry.getAll();

        assertThat(gamePlayers).containsExactly(gamePlayer);
    }

    @Test
    void isRegisteredReturnsTrueIfStorageContainsRecordWithCorrespondingPlayerEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        playerRegistry.registerEntity(player);
        boolean registered = playerRegistry.isRegistered(player);

        assertThat(registered).isTrue();
    }

    @Test
    void isRegisteredReturnsTrueIfStorageContainsEntryWithGivenUUID() {
        UUID uuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        playerRegistry.registerEntity(player);
        boolean registered = playerRegistry.isRegistered(uuid);

        assertThat(registered).isTrue();
    }

    @Test
    void deregisterRemovesGivenPlayerUuidFromPlayerContainer() {
        UUID playerUuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        playerRegistry.registerEntity(player);
        playerRegistry.deregister(playerUuid);

        assertThat(playerRegistry.findByUniqueId(playerUuid)).isEmpty();
    }

    @Test
    void registerEntityCreatesNewInstanceOfGamePlayerAndRegisterToGameStorage() {
        UUID playerId = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(eq(player), any(Hitbox.class))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitbox(player)).thenReturn(Optional.of(mock(Hitbox.class)));

        GamePlayer createdGamePlayer = playerRegistry.registerEntity(player);

        assertThat(createdGamePlayer).isEqualTo(gamePlayer);

        verify(gameContextProvider).registerEntity(playerId, GAME_KEY);
    }
}
