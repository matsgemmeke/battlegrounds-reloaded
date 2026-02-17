package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StaticActorTest {

    private static final Location LOCATION = new Location(null, 1, 1, 1);

    @Mock
    private World world;

    private StaticActor actor;

    @BeforeEach
    void setUp() {
        actor = new StaticActor(LOCATION, world);
    }

    @Test
    @DisplayName("exists always return true")
    void exists_returnsTrue() {
        boolean exists = actor.exists();

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("getLocation returns static location")
    void getLocation_returnsStaticLocation() {
        Location actorLocation = actor.getLocation();

        assertThat(actorLocation).isEqualTo(LOCATION);
    }

    @Test
    @DisplayName("getVelocity returns static vector")
    void getVelocity_returnsStaticVelocity() {
        Vector actorVelocity = actor.getVelocity();

        assertThat(actorVelocity.isZero()).isTrue();
    }

    @Test
    @DisplayName("getWorld returns static world")
    void getWorld_returnsStaticWorld() {
        World actorWorld = actor.getWorld();

        assertThat(actorWorld).isEqualTo(world);
    }
}
