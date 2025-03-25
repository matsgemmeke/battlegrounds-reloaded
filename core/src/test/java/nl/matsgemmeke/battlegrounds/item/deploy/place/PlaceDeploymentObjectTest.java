package nl.matsgemmeke.battlegrounds.item.deploy.place;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class PlaceDeploymentObjectTest {

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

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        boolean exists = object.exists();

        assertTrue(exists);
    }

    @Test
    public void shouldNotExistIfBlockTypeDoesNotEqualOriginalMaterial() {
        when(block.getType()).thenReturn(Material.STONE);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        boolean exists = object.exists();

        assertFalse(exists);
    }

    @Test
    public void getLastDamageReturnsNullIfBlockHasNotTakenDamage() {
        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        Damage lastDamage = object.getLastDamage();

        assertNull(lastDamage);
    }

    @Test
    public void getLastDamageReturnsLastDamageDealtToBlock() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        object.damage(damage);
        Damage lastDamage = object.getLastDamage();

        assertEquals(damage, lastDamage);
    }

    @Test
    public void getLocationReturnsCenterLocationOfBlock() {
        Location location = new Location(null, 1, 2, 3);
        when(block.getLocation()).thenReturn(location);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        Location objectLocation = object.getLocation();

        assertThat(objectLocation.getX()).isEqualTo(1.5);
        assertThat(objectLocation.getY()).isEqualTo(2.5);
        assertThat(objectLocation.getZ()).isEqualTo(3.5);
    }

    @Test
    public void shouldReturnSameWorldAsBlockWhereObjectIsPlacedOn() {
        World world = mock(World.class);
        when(block.getWorld()).thenReturn(world);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        World objectWorld = object.getWorld();

        assertEquals(world, objectWorld);
    }

    @Test
    public void isDeployedAlwaysReturnsTrue() {
        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        boolean deployed = object.isDeployed();

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

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        object.setHealth(health);
        object.setResistances(resistances);

        double damageDealt = object.damage(damage);

        assertThat(damageDealt).isEqualTo(expectedDamageDealt);
        assertThat(object.getHealth()).isEqualTo(expectedHealth);
    }

    @Test
    public void destroyRemovesBlock() {
        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        object.destroy();

        verify(block).setType(Material.AIR);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesIsNull() {
        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesDoesNotContainEntryForDamageType() {
        Map<DamageType, Double> resistances = Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.0);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        object.setResistances(resistances);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistanceToDamageTypeIsLargerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.5);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        object.setResistances(resistances);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsTrueIfResistanceToDamageTypeEqualsOrIsLowerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        object.setResistances(resistances);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertTrue(immune);
    }

    @Test
    public void matchesEntityAlwaysReturnsFalse() {
        Entity entity = mock(Entity.class);

        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        boolean matches = object.matchesEntity(entity);

        assertFalse(matches);
    }

    @Test
    public void shouldRemoveBlockWhenRemovingObject() {
        PlaceDeploymentObject object = new PlaceDeploymentObject(block, material);
        object.remove();

        verify(block).setType(Material.AIR);
    }
}
