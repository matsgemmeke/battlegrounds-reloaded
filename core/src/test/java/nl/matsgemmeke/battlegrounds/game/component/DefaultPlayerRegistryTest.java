package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.component.registry.DefaultPlayerRegistry;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultPlayerRegistryTest {

    private DefaultGamePlayerFactory gamePlayerFactory;
    private EntityStorage<GamePlayer> playerStorage;

    @BeforeEach
    public void setUp() {
        gamePlayerFactory = mock(DefaultGamePlayerFactory.class);
        playerStorage = new EntityStorage<>();
    }

    @Test
    public void findByEntityReturnsMatchingEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerStorage.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerStorage);
        GamePlayer result = playerRegistry.findByEntity(player);

        assertEquals(gamePlayer, result);
    }

    @Test
    public void findByEntityReturnsNoEntityIfThereIsNoMatch() {
        Player player = mock(Player.class);
        Player otherPlayer = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerStorage.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerStorage);
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

        playerStorage.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerStorage);
        GamePlayer result = playerRegistry.findByUUID(uuid);

        assertEquals(gamePlayer, result);
    }

    @Test
    public void isRegisteredReturnsTrueIfStorageContainsRecordWithCorrespondingPlayerEntity() {
        Player player = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        playerStorage.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerStorage);
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

        playerStorage.addEntity(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerStorage);
        boolean registered = playerRegistry.isRegistered(uuid);

        assertTrue(registered);
    }

    @Test
    public void registerEntityCreatesNewInstanceOfGamePlayerAndRegisterToGameStorage() {
        UUID uuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        when(gamePlayerFactory.create(player)).thenReturn(gamePlayer);

        DefaultPlayerRegistry playerRegistry = new DefaultPlayerRegistry(gamePlayerFactory, playerStorage);
        GamePlayer created = playerRegistry.registerEntity(player);

        assertEquals(playerStorage.getEntity(player), created);
    }
}
