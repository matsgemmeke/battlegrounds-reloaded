package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.assertj.core.data.Offset;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrowDeploymentObjectTest {

    @Mock
    private HitboxProvider<StaticBoundingBox> hitboxProvider;
    @Mock
    private Item item;
    @InjectMocks
    private ThrowDeploymentObject deploymentObject;

    @ParameterizedTest
    @CsvSource({ "false,true", "true,false" })
    void existsReturnsWhetherItemExists(boolean dead, boolean expectedExists) {
        when(item.isDead()).thenReturn(dead);

        boolean exists = deploymentObject.exists();

        assertThat(exists).isEqualTo(expectedExists);
    }

    @Test
    void getLastDamageReturnsNullWhenItemHasNotTakenDamage() {
        Damage lastDamage = deploymentObject.getLastDamage();

        assertThat(lastDamage).isNull();
    }

    @Test
    void getLastDamageReturnsLastDamageDealtToItem() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(true);

        deploymentObject.damage(damage);
        Damage lastDamage = deploymentObject.getLastDamage();

        assertThat(lastDamage).isEqualTo(damage);
    }

    @Test
    void getLocationReturnsSameLocationAsItem() {
        Location itemLocation = new Location(null, 1, 1, 1);
        when(item.getLocation()).thenReturn(itemLocation);

        Location deploymentObjectLocation = deploymentObject.getLocation();

        assertThat(deploymentObjectLocation).isEqualTo(itemLocation);
    }

    @Test
    void getVelocityReturnsItemVelocity() {
        Vector velocity = new Vector(1, 1, 1);
        when(item.getVelocity()).thenReturn(velocity);

        Vector result = deploymentObject.getVelocity();

        assertThat(result).isEqualTo(velocity);
    }

    @Test
    void setVelocitySetsItemVelocity() {
        Vector velocity = new Vector(1, 1, 1);

        deploymentObject.setVelocity(velocity);

        verify(item).setVelocity(velocity);
    }

    @Test
    void getWorldReturnItemWorld() {
        World itemWorld = mock(World.class);
        when(item.getWorld()).thenReturn(itemWorld);

        World deploymentObjectWorld = deploymentObject.getWorld();

        assertThat(deploymentObjectWorld).isEqualTo(itemWorld);
    }

    @Test
    void hasGravityReturnsTrueWhenItemHasGravity() {
        when(item.hasGravity()).thenReturn(true);

        boolean gravity = deploymentObject.hasGravity();

        assertThat(gravity).isTrue();
    }

    @Test
    void setGravitySetsItemGravity() {
        deploymentObject.setGravity(true);

        verify(item).setGravity(true);
    }

    @Test
    void damageReturnsZeroWhenItemDoesNotExist() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(item.isDead()).thenReturn(true);

        double damageDealt = deploymentObject.damage(damage);

        assertThat(damageDealt).isZero();
    }

    @Test
    void damageReturnsZeroWhenItemIsNotValid() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(false);

        double damageDealt = deploymentObject.damage(damage);

        assertThat(damageDealt).isZero();
    }

    static Stream<Arguments> damageScenarios() {
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
    void damageReturnsDealtDamageAndLowersHealth(
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

        deploymentObject.setHealth(health);
        deploymentObject.setResistances(resistances);
        double damageDealt = deploymentObject.damage(damage);

        assertThat(expectedDamageDealt).isEqualTo(damageDealt);
        assertThat(expectedHealth).isEqualTo(deploymentObject.getHealth());
    }

    @Test
    void getHitboxReturnsHitboxFromCurrentBoundingBox() {
        Location itemLocation = new Location(null, 1, 2, 3);
        BoundingBox boundingBox = BoundingBox.of(itemLocation, 0.1, 0.1, 0.1);
        Hitbox hitbox = new Hitbox(null, null);

        when(hitboxProvider.provideHitbox(any(StaticBoundingBox.class))).thenReturn(hitbox);
        when(item.getBoundingBox()).thenReturn(boundingBox);
        when(item.getLocation()).thenReturn(itemLocation);

        Hitbox result = deploymentObject.getHitbox();

        ArgumentCaptor<StaticBoundingBox> staticBoundingBoxCaptor = ArgumentCaptor.forClass(StaticBoundingBox.class);
        verify(hitboxProvider).provideHitbox(staticBoundingBoxCaptor.capture());

        assertThat(staticBoundingBoxCaptor.getValue()).satisfies(staticBoundingBox -> {
            assertThat(staticBoundingBox.baseLocation()).isEqualTo(itemLocation);
            assertThat(staticBoundingBox.widthX()).isEqualTo(0.2, Offset.offset(0.000001));
            assertThat(staticBoundingBox.height()).isEqualTo(0.2, Offset.offset(0.000001));
            assertThat(staticBoundingBox.widthZ()).isEqualTo(0.2, Offset.offset(0.000001));
        });

        assertThat(result).isEqualTo(hitbox);
    }

    @Test
    void getNameReturnsClassName() {
        String name = deploymentObject.getName();

        assertThat(name).isEqualTo("ThrowDeploymentObject");
    }

    static List<Arguments> resistances() {
        return List.of(
                arguments(Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.0)),
                arguments(Map.of(DamageType.BULLET_DAMAGE, 0.5))
        );
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("resistances")
    void isImmuneReturnsFalseWhenResistancesDoesNotContainEntryForGivenDamageTypeWhichEqualsZero(Map<DamageType, Double> resistances) {
        deploymentObject.setResistances(resistances);
        boolean immune = deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertThat(immune).isFalse();
    }

    @Test
    void isImmuneReturnsTrueWhenResistanceToGivenDamageTypeEqualsOrIsLowerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        deploymentObject.setResistances(resistances);
        boolean immune = deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertThat(immune).isTrue();
    }

    @Test
    void matchesEntityReturnsTrueWhenGivenEntityEqualsItem() {
        boolean matches = deploymentObject.matchesEntity(item);

        assertThat(matches).isTrue();
    }

    @Test
    void matchesEntityReturnsFalseWhenGivenEntityDoesNotEqualItem() {
        Entity entity = mock(Entity.class);

        boolean matches = deploymentObject.matchesEntity(entity);

        assertThat(matches).isFalse();
    }

    @Test
    void removeRemovesItem() {
        deploymentObject.remove();

        verify(item).remove();
    }
}
