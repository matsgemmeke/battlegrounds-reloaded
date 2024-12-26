package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlacedBlockTest {

    private Block block;
    private Material material;

    @BeforeEach
    public void setUp() {
        block = mock(Block.class);
        material = Material.WARPED_BUTTON;
    }

    @Test
    public void shouldExistIfBlockTypeEqualsOriginalMaterial() {
        when(block.getType()).thenReturn(material);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean exists = placedBlock.exists();

        assertTrue(exists);
    }

    @Test
    public void shouldNotExistIfBlockTypeDoesNotEqualOriginalMaterial() {
        when(block.getType()).thenReturn(Material.STONE);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean exists = placedBlock.exists();

        assertFalse(exists);
    }

    @Test
    public void getLocationReturnsCenterLocationOfBlock() {
        Location location = new Location(null, 1, 1, 1);
        when(block.getLocation()).thenReturn(location);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        Location blockLocation = placedBlock.getLocation();

        assertEquals(1.5, blockLocation.getX());
        assertEquals(1.5, blockLocation.getY());
        assertEquals(1.5, blockLocation.getZ());
    }

    @Test
    public void shouldReturnSameWorldAsBlockWhereObjectIsPlacedOn() {
        World world = mock(World.class);
        when(block.getWorld()).thenReturn(world);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        World objectWorld = placedBlock.getWorld();

        assertEquals(world, objectWorld);
    }

    @Test
    public void isDeployedAlwaysReturnsTrue() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean deployed = placedBlock.isDeployed();

        assertTrue(deployed);
    }

    @Test
    public void damageLowersHealthIfDamageAmountIsLowerThanHealth() {
        double health = 30.0;
        double damageAmount = 20.0;

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.setHealth(health);

        double newHealth = placedBlock.damage(damageAmount);

        assertEquals(10.0, newHealth, 0.0);
    }

    @Test
    public void damageSetsHealthToZeroIfDamageAmountIsGreaterThanHealth() {
        double health = 20.0;
        double damageAmount = 30.0;

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.setHealth(health);

        double newHealth = placedBlock.damage(damageAmount);

        assertEquals(0.0, newHealth, 0.0);
    }

    @Test
    public void destroyRemovesBlock() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.destroy();

        verify(block).setType(Material.AIR);
    }

    @Test
    public void shouldRemoveBlockWhenRemovingObject() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.remove();

        verify(block).setType(Material.AIR);
    }
}
