package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class HeldItemTest {

    private Deployer deployer;
    private Entity deployerEntity;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        deployer = mock(Deployer.class);
        deployerEntity = mock(Entity.class);
        itemStack = new ItemStack(Material.SHEARS);
    }

    @Test
    public void existsReturnsTrueIfTheDeployerEntityIsNotDead() {
        when(deployerEntity.isDead()).thenReturn(false);

        HeldItem heldItem = new HeldItem(deployer, deployerEntity, itemStack);
        boolean exists = heldItem.exists();

        assertThat(exists).isTrue();
    }

    @Test
    public void existsReturnsFalseIfTheDeployerHolderEntityIsDead() {
        when(deployerEntity.isDead()).thenReturn(true);

        HeldItem heldItem = new HeldItem(deployer, deployerEntity, itemStack);
        boolean exists = heldItem.exists();

        assertThat(exists).isFalse();
    }

    @Test
    public void getLocationReturnsTheDeployerEntityLocation() {
        Location deployerLocation = new Location(null, 0, 0, 0);
        when(deployerEntity.getLocation()).thenReturn(deployerLocation);

        HeldItem heldItem = new HeldItem(deployer, deployerEntity, itemStack);
        Location objectLocation = heldItem.getLocation();

        assertThat(deployerLocation).isEqualTo(objectLocation);
    }

    @Test
    public void getWorldReturnsTheDeployerEntityWorld() {
        World deployerWorld = mock(World.class);
        when(deployerEntity.getWorld()).thenReturn(deployerWorld);

        HeldItem heldItem = new HeldItem(deployer, deployerEntity, itemStack);
        World objectWorld = heldItem.getWorld();

        assertThat(deployerWorld).isEqualTo(objectWorld);
    }

    @Test
    public void isDeployedAlwaysReturnsFalse() {
        HeldItem heldItem = new HeldItem(deployer, deployerEntity, itemStack);
        boolean deployed = heldItem.isDeployed();

        assertThat(deployed).isFalse();
    }

    @Test
    public void removeRemovesTheItemStackFromTheHolder() {
        HeldItem heldItem = new HeldItem(deployer, deployerEntity, itemStack);
        heldItem.remove();

        verify(deployer).removeItem(itemStack);
    }
}
