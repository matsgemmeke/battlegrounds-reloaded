package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    @Spy
    private GameKey gameKey = GAME_KEY;
    @Mock
    private HitboxProvider<Player> hitboxProvider;
    @Mock
    private HitboxResolver hitboxResolver;
    @InjectMocks
    private DefaultPlayerRegistry playerRegistry;

    @Test
    @DisplayName("findByUniqueId returns empty optional when given unique id is not registered")
    void findByUniqueId_notRegistered() {
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID);

        assertThat(gamePlayerOptional).isEmpty();
    }

    @Test
    @DisplayName("findByUniqueId returns optional with matching player")
    void findByUniqueId_successful() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(eq(player), any(EntityKey.class), eq(hitboxProvider))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        Optional<GamePlayer> gamePlayerOptional = playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID);

        assertThat(gamePlayerOptional).hasValue(gamePlayer);
    }

    @Test
    @DisplayName("getAll returns all instances from the registry")
    void getAll() {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(eq(player), any(EntityKey.class), eq(hitboxProvider))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        Collection<GamePlayer> gamePlayers = playerRegistry.getAll();

        assertThat(gamePlayers).containsExactly(gamePlayer);
    }

    @Test
    @DisplayName("isRegistered returns false when given unique is not registered")
    void isRegistered_givenUniqueIdNotRegistered() {
        boolean registered = playerRegistry.isRegistered(PLAYER_UNIQUE_ID);

        assertThat(registered).isFalse();
    }

    @Test
    @DisplayName("isRegistered returns true when given unique is registered")
    void isRegistered_givenUniqueIdRegistered() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(gamePlayerFactory.create(eq(player), any(EntityKey.class), eq(hitboxProvider))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        boolean registered = playerRegistry.isRegistered(PLAYER_UNIQUE_ID);

        assertThat(registered).isTrue();
    }

    @Test
    @DisplayName("deregister removes player with matching unique id from registry")
    void deregister() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        when(gamePlayerFactory.create(eq(player), any(EntityKey.class), eq(hitboxProvider))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        playerRegistry.register(player);
        playerRegistry.deregister(PLAYER_UNIQUE_ID);

        assertThat(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).isEmpty();
    }

    @Test
    @DisplayName("register creates new instance of GamePlayer and registers it")
    void register() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        when(gamePlayerFactory.create(eq(player), any(EntityKey.class), eq(hitboxProvider))).thenReturn(gamePlayer);
        when(hitboxResolver.resolveHitboxProvider(player)).thenReturn(hitboxProvider);

        GamePlayer createdGamePlayer = playerRegistry.register(player);

        ArgumentCaptor<EntityKey> entityKeyCaptor = ArgumentCaptor.forClass(EntityKey.class);
        verify(gamePlayerFactory).create(eq(player), entityKeyCaptor.capture(), eq(hitboxProvider));

        assertThat(createdGamePlayer).isEqualTo(gamePlayer);
        assertThat(entityKeyCaptor.getValue().getValue()).isEqualTo("minecraft:player");

        verify(gameContextProvider).registerEntity(PLAYER_UNIQUE_ID, GAME_KEY);
    }
}
