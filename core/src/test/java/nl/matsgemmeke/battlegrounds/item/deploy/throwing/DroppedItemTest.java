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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class DroppedItemTest {

    private Item itemEntity;

    @BeforeEach
    public void setUp() {
        itemEntity = mock(Item.class);
    }

    @Test
    public void shouldExistIfItemEntityIsIntact() {
        when(itemEntity.isDead()).thenReturn(false);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean exists = droppedItem.exists();

        assertTrue(exists);
    }

    @Test
    public void shouldNotExistIfItemEntityDoesNotExist() {
        when(itemEntity.isDead()).thenReturn(true);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean exists = droppedItem.exists();

        assertFalse(exists);
    }

    @Test
    public void getLastDamageReturnsNullIfItemHasNotTakenDamage() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        Damage lastDamage = droppedItem.getLastDamage();

        assertNull(lastDamage);
    }

    @Test
    public void getLastDamageReturnsLastDamageDealtToItem() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(itemEntity.isDead()).thenReturn(false);
        when(itemEntity.isValid()).thenReturn(true);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.damage(damage);
        Damage lastDamage = droppedItem.getLastDamage();

        assertEquals(damage, lastDamage);
    }

    @Test
    public void returnSameLocationAsItemEntity() {
        Location itemLocation = new Location(null, 1, 1, 1);
        when(itemEntity.getLocation()).thenReturn(itemLocation);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        Location objectLocation = droppedItem.getLocation();

        assertEquals(itemLocation, objectLocation);
    }

    @Test
    public void getVelocityReturnsItemEntityVelocity() {
        Vector velocity = new Vector(1, 1, 1);
        when(itemEntity.getVelocity()).thenReturn(velocity);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        Vector result = droppedItem.getVelocity();

        assertEquals(velocity, result);
    }

    @Test
    public void setVelocitySetsItemEntityVelocity() {
        Vector velocity = new Vector(1, 1, 1);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.setVelocity(velocity);

        verify(itemEntity).setVelocity(velocity);
    }

    @Test
    public void returnSameWorldAsItemEntity() {
        World itemWorld = mock(World.class);
        when(itemEntity.getWorld()).thenReturn(itemWorld);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        World objectWorld = droppedItem.getWorld();

        assertEquals(itemWorld, objectWorld);
    }

    @Test
    public void hasGravityReturnsItemEntityGravity() {
        when(itemEntity.hasGravity()).thenReturn(true);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean result = droppedItem.hasGravity();

        assertTrue(result);
    }

    @Test
    public void setGravitySetsItemEntityGravity() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.setGravity(true);

        verify(itemEntity).setGravity(true);
    }

    @Test
    public void damageReturnsZeroIfItemEntityDoesNotExist() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(itemEntity.isDead()).thenReturn(true);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        double damageDealt = droppedItem.damage(damage);

        assertEquals(0.0, damageDealt);
    }

    @Test
    public void damageReturnsZeroIfItemEntityIsNotValid() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(itemEntity.isDead()).thenReturn(false);
        when(itemEntity.isValid()).thenReturn(false);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        double damageDealt = droppedItem.damage(damage);

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

        when(itemEntity.isDead()).thenReturn(false);
        when(itemEntity.isValid()).thenReturn(true);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.setHealth(health);
        droppedItem.setResistances(resistances);

        double damageDealt = droppedItem.damage(damage);

        assertEquals(expectedDamageDealt, damageDealt);
        assertEquals(expectedHealth, droppedItem.getHealth());
    }

    @Test
    public void destroyRemovesTheItemEntity() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.destroy();

        verify(itemEntity).remove();
    }

    @Test
    public void isDeployedAlwaysReturnsTrue() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean deployed = droppedItem.isDeployed();

        assertTrue(deployed);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesIsNull() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean immune = droppedItem.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistancesDoesNotContainEntryForDamageType() {
        Map<DamageType, Double> resistances = Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.0);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.setResistances(resistances);
        boolean immune = droppedItem.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsFalseIfResistanceToDamageTypeIsLargerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.5);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.setResistances(resistances);
        boolean immune = droppedItem.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertFalse(immune);
    }

    @Test
    public void isImmuneReturnsTrueIfResistanceToDamageTypeEqualsOrIsLowerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.setResistances(resistances);
        boolean immune = droppedItem.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertTrue(immune);
    }

    @Test
    public void matchesEntityReturnsTrueIfGivenEntityEqualsItemEntity() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean matches = droppedItem.matchesEntity(itemEntity);

        assertTrue(matches);
    }

    @Test
    public void matchesEntityReturnsFalseIfGivenEntityDoesNotEqualItemEntity() {
        Entity entity = mock(Entity.class);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        boolean matches = droppedItem.matchesEntity(entity);

        assertFalse(matches);
    }

    @Test
    public void removeItemEntityWhenRemovingObject() {
        DroppedItem droppedItem = new DroppedItem(itemEntity);
        droppedItem.remove();

        verify(itemEntity).remove();
    }
}
