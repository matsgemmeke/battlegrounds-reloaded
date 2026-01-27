package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemActorTest {

    @Mock
    private Item item;
    @InjectMocks
    private ItemActor actor;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("exists returns whether item is valid")
    void exists_returnsItemValidState(boolean valid, boolean shouldExist) {
        when(item.isValid()).thenReturn(valid);

        boolean exists = actor.exists();

        assertThat(exists).isEqualTo(shouldExist);
    }

    @Test
    @DisplayName("getLocation returns location of item")
    void getLocation_returnsItemLocation() {
        Location itemLocation = new Location(null, 1, 1, 1);

        when(item.getLocation()).thenReturn(itemLocation);

        Location actorLocation = actor.getLocation();

        assertThat(actorLocation).isEqualTo(itemLocation);
    }

    @Test
    @DisplayName("getVelocity returns velocity of item")
    void getVelocity_returnsItemVelocity() {
        Vector itemVelocity = new Vector();

        when(item.getVelocity()).thenReturn(itemVelocity);

        Vector actorVelocity = actor.getVelocity();

        assertThat(actorVelocity).isEqualTo(itemVelocity);
    }

    @Test
    @DisplayName("getWorld returns world of item")
    void getWorld_returnsItemWorld() {
        World itemWorld = mock(World.class);

        when(item.getWorld()).thenReturn(itemWorld);

        World actorWorld = actor.getWorld();

        assertThat(actorWorld).isEqualTo(itemWorld);
    }
}
