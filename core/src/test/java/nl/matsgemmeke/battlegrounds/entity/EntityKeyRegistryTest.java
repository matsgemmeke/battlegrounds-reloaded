package nl.matsgemmeke.battlegrounds.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntityKeyRegistryTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();
    private static final EntityKey ENTITY_KEY = EntityKey.custom("bot");

    private EntityKeyRegistry entityKeyRegistry;

    @BeforeEach
    void setUp() {
        entityKeyRegistry = new EntityKeyRegistry();
    }

    @Test
    @DisplayName("getEntityKey returns empty optional when given unique id was registered but is removed")
    void getEntityKey_removed() {
        entityKeyRegistry.register(UNIQUE_ID, ENTITY_KEY);
        entityKeyRegistry.remove(UNIQUE_ID);

        Optional<EntityKey> entityKeyOptional = entityKeyRegistry.getEntityKey(UNIQUE_ID);

        assertThat(entityKeyOptional).isEmpty();
    }

    @Test
    @DisplayName("getEntityKey returns optional with corresponding entity key for given unique id")
    void getEntityKey_successful() {
        entityKeyRegistry.register(UNIQUE_ID, ENTITY_KEY);

        Optional<EntityKey> entityKeyOptional = entityKeyRegistry.getEntityKey(UNIQUE_ID);

        assertThat(entityKeyOptional).hasValue(ENTITY_KEY);
    }
}
