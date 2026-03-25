package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrimeDeploymentObjectTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.SHEARS);

    @Mock
    private Deployer deployer;
    @Mock
    private Entity deployerEntity;

    private PrimeDeploymentObject deploymentObject;

    @BeforeEach
    void setUp() {
        deploymentObject = new PrimeDeploymentObject(deployer, deployerEntity, ITEM_STACK);
    }

    @ParameterizedTest
    @CsvSource({ "true,false", "false,true" })
    @DisplayName("exists returns whether deployer entity is not dead")
    void exists_returnsDeployerDead(boolean dead, boolean expectedExists) {
        when(deployerEntity.isDead()).thenReturn(dead);

        boolean exists = deploymentObject.exists();

        assertThat(exists).isEqualTo(expectedExists);
    }

    @Test
    @DisplayName("getLocation returns the deployer entity's location")
    void getLocation_returnsEntityLocation() {
        Location deployerLocation = new Location(null, 0, 0, 0);
        when(deployerEntity.getLocation()).thenReturn(deployerLocation);

        Location objectLocation = deploymentObject.getLocation();

        assertThat(deployerLocation).isEqualTo(objectLocation);
    }

    @Test
    @DisplayName("getVelocity returns the deployer entity's velocity")
    void getVelocity_returnsEntityVelocity() {
        Vector deployerVelocity = new Vector(1.0, 2.0, 3.0);
        when(deployerEntity.getVelocity()).thenReturn(deployerVelocity);

        Vector objectVelocity = deploymentObject.getVelocity();

        assertThat(objectVelocity).isEqualTo(deployerVelocity);
    }


    @Test
    @DisplayName("getWorld returns the deployer entity's world")
    void getWorld_returnsEntityWorld() {
        World deployerWorld = mock(World.class);
        when(deployerEntity.getWorld()).thenReturn(deployerWorld);

        World objectWorld = deploymentObject.getWorld();

        assertThat(deployerWorld).isEqualTo(objectWorld);
    }

    @Test
    @DisplayName("remove removes the item stack from the user")
    void remove_removesItemStackFromUser() {
        deploymentObject.remove();

        verify(deployer).removeItem(ITEM_STACK);
    }
}
