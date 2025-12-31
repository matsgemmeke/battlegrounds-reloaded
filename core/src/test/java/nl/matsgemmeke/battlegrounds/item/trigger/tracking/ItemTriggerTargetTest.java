package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
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
class ItemTriggerTargetTest {

    private static final Location ITEM_LOCATION = new Location(null, 1, 1, 1);
    private static final Vector ITEM_VELOCITY = new Vector();

    @Mock
    private Item item;
    @InjectMocks
    private ItemTriggerTarget triggerTarget;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    void existsReturnsWetherItemIsValid(boolean valid, boolean shouldExist) {
        when(item.isValid()).thenReturn(valid);

        boolean exists = triggerTarget.exists();

        assertThat(exists).isEqualTo(shouldExist);
    }

    @Test
    void getLocationReturnsItemLocation() {
        when(item.getLocation()).thenReturn(ITEM_LOCATION);

        Location result = triggerTarget.getLocation();

        assertThat(result).isEqualTo(ITEM_LOCATION);
    }

    @Test
    void getVelocityReturnsItemVelocity() {
        when(item.getVelocity()).thenReturn(ITEM_VELOCITY);

        Vector result = triggerTarget.getVelocity();

        assertThat(result).isEqualTo(ITEM_VELOCITY);
    }

    @Test
    void getWorldReturnsItemWorld() {
        World world = mock(World.class);

        when(item.getWorld()).thenReturn(world);

        World result = triggerTarget.getWorld();

        assertThat(result).isEqualTo(world);
    }
}
