package nl.matsgemmeke.battlegrounds.game.component.effect;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExplosionAttributorRegistryTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID ENTITY_ID = UUID.randomUUID();

    @Mock
    private GameContextProvider gameContextProvider;

    private ExplosionAttributorRegistry explosionAttributorRegistry;

    @BeforeEach
    void setUp() {
        explosionAttributorRegistry = new ExplosionAttributorRegistry(gameContextProvider, GAME_KEY);
    }

    @Test
    @DisplayName("isAttributor returns false when given entity id is not registered")
    void isAttributor_unregisteredEntity() {
        ExplosionAttributor attributor = new ExplosionAttributor(UUID.randomUUID());

        explosionAttributorRegistry.addAttributor(attributor);
        boolean isAttributor = explosionAttributorRegistry.isAttributor(ENTITY_ID);

        assertThat(isAttributor).isFalse();
    }

    @Test
    @DisplayName("isAttributor returns true when given entity id is registered")
    void isAttributor_registeredEntity() {
        ExplosionAttributor attributor = new ExplosionAttributor(ENTITY_ID);

        explosionAttributorRegistry.addAttributor(attributor);
        boolean isAttributor = explosionAttributorRegistry.isAttributor(ENTITY_ID);

        assertThat(isAttributor).isTrue();
    }
}
