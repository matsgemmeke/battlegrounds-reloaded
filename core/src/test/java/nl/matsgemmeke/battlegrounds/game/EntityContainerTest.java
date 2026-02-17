package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityContainerTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();

    @Mock
    private GamePlayer gamePlayer;

    @Test
    void addEntityAddsEntityToCollection() {
        when(gamePlayer.getUniqueId()).thenReturn(UNIQUE_ID);

        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(gamePlayer);

        assertThat(container.getEntity(UNIQUE_ID)).hasValue(gamePlayer);
    }

    @Test
    void getEntityReturnsEmptyOptionalWhenThereIsNoCorrespondingEntity() {
        when(gamePlayer.getUniqueId()).thenReturn(UNIQUE_ID);

        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(gamePlayer);
        Optional<GamePlayer> entityOptional = container.getEntity(UUID.randomUUID());

        assertThat(entityOptional).isEmpty();
    }

    @Test
    void getEntitiesReturnsAllStoredInstances() {
        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(gamePlayer);
        Collection<GamePlayer> players = container.getEntities();

        assertThat(players).containsExactly(gamePlayer);
    }

    @Test
    void removeEntityRemovesEntityFromCollection() {
        EntityContainer<GamePlayer> container = new EntityContainer<>();
        container.addEntity(gamePlayer);
        container.removeEntity(UNIQUE_ID);

        assertThat(container.getEntity(UNIQUE_ID)).isEmpty();
    }
}
