package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityContainerTest {

    private GamePlayer gamePlayer;
    private Player player;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);
    }

    @Test
    void addEntityAddsEntityToCollection() {
        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(gamePlayer);

        assertThat(container.getEntity(player)).hasValue(gamePlayer);
    }

    @Test
    void getEntityReturnsEmptyOptionalWhenThereIsNoCorrespondingEntity() {
        GamePlayer otherPlayer = this.createNewGamePlayer();

        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(otherPlayer);
        Optional<GamePlayer> entityOptional = container.getEntity(player);

        assertThat(entityOptional).isEmpty();
    }

    @Test
    void getEntitiesReturnsAllStoredInstances() {
        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(gamePlayer);
        Collection<GamePlayer> players = container.getEntities();

        assertThat(players).containsExactly(gamePlayer);
    }

    private GamePlayer createNewGamePlayer() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        return gamePlayer;
    }

    @Test
    void removeEntityRemovesEntityFromCollection() {
        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(gamePlayer);
        container.removeEntity(uuid);

        assertThat(container.getEntity(uuid)).isEmpty();
    }
}
