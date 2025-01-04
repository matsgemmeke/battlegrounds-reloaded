package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
    public void getLastDamageReturnsNullIfBlockHasNotTakenDamage() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        Damage lastDamage = placedBlock.getLastDamage();

        assertNull(lastDamage);
    }

    @Test
    public void getLastDamageReturnsLastDamageDealtToBlock() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.damage(damage);
        Damage lastDamage = placedBlock.getLastDamage();

        assertEquals(damage, lastDamage);
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

    @NotNull
    private static Stream<Arguments> damageScenarios() {
        return Stream.of(
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, null),
                arguments(10.0, 5.0, 100.0, 95.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.BULLET_DAMAGE, 0.5)),
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.5)),
                arguments(1000.0, 1000.0, 100.0, 0.0, DamageType.BULLET_DAMAGE, null)
        );
    }

    @ParameterizedTest
    @MethodSource("damageScenarios")
    public void damageReturnsDealtDamageAndLowersHealth(
            double damageAmount,
            double expectedDamageDealt,
            double health,
            double expectedHealth,
            DamageType damageType,
            Map<DamageType, Double> resistances
    ) {
        Damage damage = new Damage(damageAmount, damageType);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.setHealth(health);
        placedBlock.setResistances(resistances);

        double damageDealt = placedBlock.damage(damage);

        assertEquals(expectedDamageDealt, damageDealt);
        assertEquals(expectedHealth, placedBlock.getHealth());
    }

    @Test
    public void destroyRemovesBlock() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.destroy();

        verify(block).setType(Material.AIR);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesIsNull() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean immune = placedBlock.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesDoesNotContainEntryForDamageType() {
        Map<DamageType, Double> resistances = Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.0);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.setResistances(resistances);
        boolean immune = placedBlock.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistanceToDamageTypeIsLargerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.5);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.setResistances(resistances);
        boolean immune = placedBlock.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsTrueIfResistanceToDamageTypeEqualsOrIsLowerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.setResistances(resistances);
        boolean immune = placedBlock.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertTrue(immune);
    }

    @Test
    public void matchesEntityAlwaysReturnsFalse() {
        Entity entity = mock(Entity.class);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean matches = placedBlock.matchesEntity(entity);

        assertFalse(matches);
    }

    @Test
    public void shouldRemoveBlockWhenRemovingObject() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.remove();

        verify(block).setType(Material.AIR);
    }
}
