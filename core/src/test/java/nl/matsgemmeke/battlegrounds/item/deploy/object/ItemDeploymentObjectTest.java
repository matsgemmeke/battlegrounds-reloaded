package nl.matsgemmeke.battlegrounds.item.deploy.object;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import org.assertj.core.data.Offset;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemDeploymentObjectTest {

    @Mock
    private DestructionListener destructionListener;
    @Mock
    private HitboxProvider<StaticBoundingBox> hitboxProvider;
    @Mock
    private Item item;
    @InjectMocks
    private ItemDeploymentObject deploymentObject;

    @ParameterizedTest
    @CsvSource({ "false,true", "true,false" })
    @DisplayName("exists returns whether item exists")
    void exists(boolean dead, boolean expectedExists) {
        when(item.isDead()).thenReturn(dead);

        boolean exists = deploymentObject.exists();

        assertThat(exists).isEqualTo(expectedExists);
    }

    @Test
    @DisplayName("getLocation returns item location")
    void getLocation() {
        Location itemLocation = new Location(null, 1, 1, 1);
        when(item.getLocation()).thenReturn(itemLocation);

        Location deploymentObjectLocation = deploymentObject.getLocation();

        assertThat(deploymentObjectLocation).isEqualTo(itemLocation);
    }

    @Test
    @DisplayName("getVelocity returns item velocity")
    void getVelocity() {
        Vector velocity = new Vector(1, 1, 1);
        when(item.getVelocity()).thenReturn(velocity);

        Vector result = deploymentObject.getVelocity();

        assertThat(result).isEqualTo(velocity);
    }

    @Test
    @DisplayName("setVelocity sets item velocity")
    void setVelocitySetsItemVelocity() {
        Vector velocity = new Vector(1, 1, 1);

        deploymentObject.setVelocity(velocity);

        verify(item).setVelocity(velocity);
    }

    @Test
    @DisplayName("getWorld returns item world")
    void getWorld() {
        World itemWorld = mock(World.class);
        when(item.getWorld()).thenReturn(itemWorld);

        World deploymentObjectWorld = deploymentObject.getWorld();

        assertThat(deploymentObjectWorld).isEqualTo(itemWorld);
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("hasGravity returns whether item has gravity")
    void hasGravity(boolean gravity, boolean expected) {
        when(item.hasGravity()).thenReturn(gravity);

        boolean result = deploymentObject.hasGravity();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("setGravity sets item gravity")
    void setGravity() {
        deploymentObject.setGravity(true);

        verify(item).setGravity(true);
    }

    @Test
    @DisplayName("damage returns zero when item does not exists")
    void damage_itemNotExists() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE, HitboxComponentType.TORSO);

        when(item.isDead()).thenReturn(true);

        double damageDealt = deploymentObject.damage(damage);

        assertThat(damageDealt).isZero();
    }

    @Test
    @DisplayName("damage returns zero when item is not valid")
    void damage_itemNotValid() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE, HitboxComponentType.TORSO);

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(false);

        double damageDealt = deploymentObject.damage(damage);

        assertThat(damageDealt).isZero();
    }

    static Stream<Arguments> nonLethalDamageScenarios() {
        return Stream.of(
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, Map.of()),
                arguments(10.0, 5.0, 100.0, 95.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.BULLET_DAMAGE, 0.5)),
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.5)),
                arguments(1.0, 1.0, 100.0, 100.0, DamageType.ENVIRONMENTAL_DAMAGE, Map.of())
        );
    }

    @ParameterizedTest
    @MethodSource("nonLethalDamageScenarios")
    @DisplayName("damage returns dealt damage and lowers health")
    void damage_normalDamage(
            double damageAmount,
            double expectedDamageDealt,
            double health,
            double expectedHealth,
            DamageType damageType,
            Map<DamageType, Double> resistances
    ) {
        Damage damage = new Damage(damageAmount, damageType, HitboxComponentType.TORSO);

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(true);

        deploymentObject.setHealth(health);
        resistances.forEach((key, value) -> deploymentObject.addResistance(key, value));
        double damageDealt = deploymentObject.damage(damage);

        assertThat(expectedDamageDealt).isEqualTo(damageDealt);
        assertThat(expectedHealth).isEqualTo(deploymentObject.getHealth());
    }

    static Stream<Arguments> lethalDamageScenarios() {
        return Stream.of(
                arguments(1000.0, 1000.0, 100.0, 0.0, DamageType.BULLET_DAMAGE),
                arguments(4.0, 4.0, 100.0, 0.0, DamageType.ENVIRONMENTAL_DAMAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("lethalDamageScenarios")
    @DisplayName("damage returns dealt damage and calls destruction listener when health is zero or less")
    void damage_destroyed(
            double damageAmount,
            double expectedDamageDealt,
            double health,
            double expectedHealth,
            DamageType damageType
    ) {
        Damage damage = new Damage(damageAmount, damageType, HitboxComponentType.TORSO);

        when(item.isDead()).thenReturn(false);
        when(item.isValid()).thenReturn(true);

        deploymentObject.setHealth(health);
        double damageDealt = deploymentObject.damage(damage);

        assertThat(expectedDamageDealt).isEqualTo(damageDealt);
        assertThat(expectedHealth).isEqualTo(deploymentObject.getHealth());

        verify(destructionListener).onDestroyed(damage);
    }

    @Test
    @DisplayName("getHitbox returns hitbox from current bounding box")
    void getHitbox() {
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
    @DisplayName("matchesEntity returns true when given entity equals item")
    void matchesEntity_sameEntity() {
        boolean matches = deploymentObject.matchesEntity(item);

        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("matchesEntity returns false when given entity does not equal item")
    void matchesEntity_differentEntity() {
        Entity entity = mock(Entity.class);

        boolean matches = deploymentObject.matchesEntity(entity);

        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("remove removes item")
    void remove() {
        deploymentObject.remove();

        verify(item).remove();
    }
}
