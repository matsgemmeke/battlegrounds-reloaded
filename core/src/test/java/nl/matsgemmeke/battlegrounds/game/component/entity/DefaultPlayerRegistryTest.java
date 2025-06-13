package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityContainer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultPlayerRegistryTest {

    private DefaultGamePlayerFactory gamePlayerFactory;
    private EntityContainer<GamePlayer> playerContainer;

    @BeforeEach
    public void setUp() {
        gamePlayerFactory = mock(DefaultGamePlayerFactory.class);
        playerContainer = new EntityContainer<>();
    }

    @Test
    public void findByEntityReturnsMatchingEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerContainer.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        GamePlayer result = playerRegistry.findByEntity(player);

        assertEquals(gamePlayer, result);
    }

    @Test
    public void findByEntityReturnsNoEntityIfThereIsNoMatch() {
        Player player = mock(Player.class);
        Player otherPlayer = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerContainer.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        GamePlayer result = playerRegistry.findByEntity(otherPlayer);

        assertNull(result);
    }

    @Test
    public void findByUUIDReturnsMatchingEntity() {
        UUID uuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerContainer.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        GamePlayer result = playerRegistry.findByUUID(uuid);

        assertEquals(gamePlayer, result);
    }

    @Test
    public void getAllReturnsPlayersFromStorage() {
        UUID playerUuid = UUID.randomUUID();

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(playerUuid);

        playerContainer.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        Collection<GamePlayer> gamePlayers = playerRegistry.getAll();

        assertThat(gamePlayers).containsExactly(gamePlayer);
    }

    @Test
    public void isRegisteredReturnsTrueIfStorageContainsRecordWithCorrespondingPlayerEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerContainer.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        boolean registered = playerRegistry.isRegistered(player);

        assertTrue(registered);
    }

    @Test
    public void isRegisteredReturnsTrueIfStorageContainsEntryWithGivenUUID() {
        UUID uuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerContainer.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        boolean registered = playerRegistry.isRegistered(uuid);

        assertTrue(registered);
    }

    @Test
    public void deregisterRemovesGivenPlayerUuidFromPlayerContainer() {
        UUID playerUuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        playerRegistry.registerEntity(player);
        playerRegistry.deregister(playerUuid);

        assertThat(playerRegistry.findByUUID(playerUuid)).isNull();
    }

    @Test
    public void registerEntityCreatesNewInstanceOfGamePlayerAndRegisterToGameStorage() {
        UUID uuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerContainer);
        GamePlayer created = playerRegistry.registerEntity(player);

        assertEquals(playerContainer.getEntity(player), created);
    }
}
