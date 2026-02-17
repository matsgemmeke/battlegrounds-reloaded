package nl.matsgemmeke.battlegrounds.item.actor;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeldItemActorTest {

    @Mock
    private Deployer deployer;
    @Mock
    private GameEntity gameEntity;
    @Mock
    private ItemStack itemStack;
    @InjectMocks
    private HeldItemActor actor;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("exists returns whether game entity is valid")
    void exists_returnsGameEntityValidState(boolean valid, boolean shouldExist) {
        when(gameEntity.isValid()).thenReturn(valid);

        boolean exists = actor.exists();

        assertThat(exists).isEqualTo(shouldExist);
    }

    @Test
    @DisplayName("getLocation returns game entity location with hand height offset")
    void getLocation_returnsGameEntityHandLocation() {
        Location gameEntityLocation = new Location(null, 1.0, 1.0, 1.0);

        when(gameEntity.getLocation()).thenReturn(gameEntityLocation);

        Location actorLocation = actor.getLocation();

        assertThat(actorLocation.getX()).isEqualTo(1.0);
        assertThat(actorLocation.getY()).isEqualTo(2.0);
        assertThat(actorLocation.getZ()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("getVelocity returns game entity's velocity")
    void getVelocity_returnsGameEntityVelocity() {
        Vector gameEntityVelocity = new Vector();

        when(gameEntity.getVelocity()).thenReturn(gameEntityVelocity);

        Vector actorVelocity = actor.getVelocity();

        assertThat(actorVelocity).isEqualTo(gameEntityVelocity);
    }

    @Test
    @DisplayName("getWorld returns game entity's world")
    void getWorld_returnsGameEntityWorld() {
        World gameEntityWorld = mock(World.class);

        when(gameEntity.getWorld()).thenReturn(gameEntityWorld);

        World actorWorld = actor.getWorld();

        assertThat(actorWorld).isEqualTo(gameEntityWorld);
    }

    @Test
    @DisplayName("remove removes ItemStack from deployer")
    void remove_removesItemStack() {
        actor.remove();

        verify(deployer).removeItem(itemStack);
    }
}
