package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
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

public class ThrowDeploymentObjectTest {

    private Item item;

    @BeforeEach
    public void setUp() {
        item = mock(Item.class);
    }

    @Test
    public void existsReturnsTrueIfItemIsIntact() {
        when(item.isDead()).thenReturn(false);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        boolean exists = object.exists();

        assertTrue(exists);
    }

    @Test
    public void existsReturnsFalseIfItemDoesNotExist() {
        when(item.isDead()).thenReturn(true);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        boolean exists = object.exists();

        assertFalse(exists);
    }

    @Test
    public void getLastDamageReturnsNullIfItemHasNotTakenDamage() {
        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        Damage lastDamage = object.getLastDamage();

        assertNull(lastDamage);
    }

    @Test
    public void getLastDamageReturnsLastDamageDealtToItem() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(true);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.damage(damage);
        Damage lastDamage = object.getLastDamage();

        assertEquals(damage, lastDamage);
    }

    @Test
    public void getLocationReturnsSameLocationAsItem() {
        Location itemLocation = new Location(null, 1, 1, 1);
        when(item.getLocation()).thenReturn(itemLocation);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        Location objectLocation = object.getLocation();

        assertEquals(itemLocation, objectLocation);
    }

    @Test
    public void getVelocityReturnsItemVelocity() {
        Vector velocity = new Vector(1, 1, 1);
        when(item.getVelocity()).thenReturn(velocity);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        Vector result = object.getVelocity();

        assertEquals(velocity, result);
    }

    @Test
    public void setVelocitySetsItemVelocity() {
        Vector velocity = new Vector(1, 1, 1);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setVelocity(velocity);

        verify(item).setVelocity(velocity);
    }

    @Test
    public void getWorldReturnItemWorld() {
        World itemWorld = mock(World.class);
        when(item.getWorld()).thenReturn(itemWorld);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        World objectWorld = object.getWorld();

        assertEquals(itemWorld, objectWorld);
    }

    @Test
    public void hasGravityReturnsTrueWhenItemHasGravity() {
        when(item.hasGravity()).thenReturn(true);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        boolean gravity = object.hasGravity();

        assertThat(gravity).isTrue();
    }

    @Test
    public void setGravitySetsItemGravity() {
        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setGravity(true);

        verify(item).setGravity(true);
    }

    @Test
    public void damageReturnsZeroIfItemDoesNotExist() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(item.isDead()).thenReturn(true);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        double damageDealt = object.damage(damage);

        assertEquals(0.0, damageDealt);
    }

    @Test
    public void damageReturnsZeroIfItemIsNotValid() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(false);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        double damageDealt = object.damage(damage);

        assertEquals(0.0, damageDealt);
    }

    @NotNull
    private static Stream<Arguments> damageScenarios() {
        return Stream.of(
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, null),
                arguments(10.0, 5.0, 100.0, 95.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.BULLET_DAMAGE, 0.5)),
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.5)),
                arguments(1000.0, 1000.0, 100.0, 0.0, DamageType.BULLET_DAMAGE, null),
                arguments(1.0, 1.0, 100.0, 100.0, DamageType.ENVIRONMENTAL_DAMAGE, null),
                arguments(4.0, 4.0, 100.0, 0.0, DamageType.ENVIRONMENTAL_DAMAGE, null)
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

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(true);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setHealth(health);
        object.setResistances(resistances);

        double damageDealt = object.damage(damage);

        assertThat(expectedDamageDealt).isEqualTo(damageDealt);
        assertThat(expectedHealth).isEqualTo(object.getHealth());
    }

    @Test
    public void destroyRemovesItem() {
        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.destroy();

        verify(item).remove();
    }

    @Test
    public void isDeployedAlwaysReturnsTrue() {
        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        boolean deployed = object.isDeployed();

        assertTrue(deployed);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesIsNull() {
        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesDoesNotContainEntryForDamageType() {
        Map<DamageType, Double> resistances = Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.0);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setResistances(resistances);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistanceToDamageTypeIsLargerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.5);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setResistances(resistances);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsTrueIfResistanceToDamageTypeEqualsOrIsLowerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setResistances(resistances);
        boolean immune = object.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertTrue(immune);
    }

    @Test
    public void matchesEntityReturnsTrueIfGivenEntityEqualsItem() {
        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        boolean matches = object.matchesEntity(item);

        assertTrue(matches);
    }

    @Test
    public void matchesEntityReturnsFalseIfGivenEntityDoesNotEqualItem() {
        Entity entity = mock(Entity.class);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        boolean matches = object.matchesEntity(entity);

        assertFalse(matches);
    }

    @Test
    public void removeRemovesItem() {
        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.remove();

        verify(item).remove();
    }
}
