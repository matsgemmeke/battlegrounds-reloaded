package nl.matsgemmeke.battlegrounds.game.component.projectile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectileRegistryTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();

    private ProjectileRegistry projectileRegistry;

    @BeforeEach
    void setUp() {
        projectileRegistry = new ProjectileRegistry();
    }

    @Test
    void isRegisteredReturnsTrueWhenGivenUniqueIdIsRegistered() {
        projectileRegistry.register(UNIQUE_ID);
        boolean registered = projectileRegistry.isRegistered(UNIQUE_ID);

        assertThat(registered).isTrue();
    }

    @Test
    void isRegisteredReturnsFalseWhenGivenUniqueIdIsNotRegistered() {
        boolean registered = projectileRegistry.isRegistered(UNIQUE_ID);

        assertThat(registered).isFalse();
    }

    @Test
    void unregisterRemovesGivenUniqueId() {
        projectileRegistry.register(UNIQUE_ID);
        projectileRegistry.unregister(UNIQUE_ID);
        boolean registered = projectileRegistry.isRegistered(UNIQUE_ID);

        assertThat(registered).isFalse();
    }
}
