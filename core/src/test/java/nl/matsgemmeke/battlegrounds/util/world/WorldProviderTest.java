package nl.matsgemmeke.battlegrounds.util.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class WorldProviderTest {

    private static final String WORLD = "world";

    private final WorldProvider worldProvider = new WorldProvider();

    @Test
    @DisplayName("getWorld returns empty optional when given world name is not found")
    void getWorld_notFound() {
        MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(() -> Bukkit.getWorld(WORLD)).thenReturn(null);

        Optional<World> worldOptional = worldProvider.getWorld(WORLD);

        assertThat(worldOptional).isEmpty();

        bukkitMock.close();
    }

    @Test
    @DisplayName("getWorld returns optional with matching world")
    void getWorld_successful() {
        World world = mock(World.class);

        MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(() -> Bukkit.getWorld(WORLD)).thenReturn(world);

        Optional<World> worldOptional = worldProvider.getWorld(WORLD);

        assertThat(worldOptional).hasValue(world);

        bukkitMock.close();
    }
}
