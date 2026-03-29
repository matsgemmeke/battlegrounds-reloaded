package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceDeploymentObjectTest {

    private static final Material MATERIAL = Material.WARPED_BUTTON;

    @Mock
    private Block block;
    @Mock
    private DestructionListener destructionListener;
    @Mock
    private HitboxProvider<StaticBoundingBox> hitboxProvider;

    private PlaceDeploymentObject deploymentObject;

    @BeforeEach
    void setUp() {
        deploymentObject = new PlaceDeploymentObject(block, MATERIAL, hitboxProvider, destructionListener);
    }

    @ParameterizedTest
    @CsvSource({ "WARPED_BUTTON,true", "STONE,false" })
    public void existsReturnsWhetherBlockTypeEqualsOriginalMaterial(Material material, boolean expectedResult) {
        when(block.getType()).thenReturn(material);

        boolean exists = deploymentObject.exists();

        assertThat(exists).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("getLastDamage returns empty optional when block has not taken damage")
    void getLastDamage_noDamageYet() {
        Optional<Damage> lastDamageOptional = deploymentObject.getLastDamage();

        assertThat(lastDamageOptional).isEmpty();
    }

    @Test
    @DisplayName("getLastDamage returns optional with last damage dealt to block")
    void getLastDamage_returnsLastDamage() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        deploymentObject.damage(damage);
        Optional<Damage> lastDamageOptional = deploymentObject.getLastDamage();

        assertThat(lastDamageOptional).hasValue(damage);
    }

    @Test
    void getLocationReturnsCenterLocationOfBlock() {
        Location location = new Location(null, 1, 2, 3);
        when(block.getLocation()).thenReturn(location);

        Location objectLocation = deploymentObject.getLocation();

        assertThat(objectLocation.getX()).isEqualTo(1.5);
        assertThat(objectLocation.getY()).isEqualTo(2.5);
        assertThat(objectLocation.getZ()).isEqualTo(3.5);
    }

    @Test
    void getVelocityAlwaysReturnZeroVector() {
        Vector velocity = deploymentObject.getVelocity();

        assertThat(velocity.getX()).isZero();
        assertThat(velocity.getY()).isZero();
        assertThat(velocity.getZ()).isZero();
    }

    @Test
    void getWorldReturnsSameWorldAsBlock() {
        World world = mock(World.class);
        when(block.getWorld()).thenReturn(world);

        World objectWorld = deploymentObject.getWorld();

        assertThat(objectWorld).isEqualTo(world);
    }

    static Stream<Arguments> damageScenarios() {
        return Stream.of(
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, null),
                arguments(10.0, 5.0, 100.0, 95.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.BULLET_DAMAGE, 0.5)),
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.5))
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

        deploymentObject.setHealth(health);
        deploymentObject.setResistances(resistances);
        double damageDealt = deploymentObject.damage(damage);

        assertThat(damageDealt).isEqualTo(expectedDamageDealt);
        assertThat(deploymentObject.getHealth()).isEqualTo(expectedHealth);
    }

    @Test
    void damageReturnsDealtDamageAndCallsDestructionListenerWhenHealthIsBelowZero() {
        Damage damage = new Damage(20.0, DamageType.BULLET_DAMAGE);

        deploymentObject.setHealth(10.0);
        double damageDealt = deploymentObject.damage(damage);

        assertThat(damageDealt).isEqualTo(20.0);
        assertThat(deploymentObject.getHealth()).isZero();

        verify(destructionListener).onDestroyed(damage);
    }

    @Test
    void getHitboxReturnsHitboxFromCurrentBoundingBox() {
        Location blockLocation = new Location(null, 1, 2, 3);
        Hitbox hitbox = new Hitbox(null, null);

        when(block.getLocation()).thenReturn(blockLocation);
        when(hitboxProvider.provideHitbox(any(StaticBoundingBox.class))).thenReturn(hitbox);

        Hitbox result = deploymentObject.getHitbox();

        ArgumentCaptor<StaticBoundingBox> boundingBoxCaptor = ArgumentCaptor.forClass(StaticBoundingBox.class);
        verify(hitboxProvider).provideHitbox(boundingBoxCaptor.capture());

        assertThat(boundingBoxCaptor.getValue()).satisfies(boundingBox -> {
            assertThat(boundingBox.baseLocation()).isEqualTo(blockLocation);
            assertThat(boundingBox.widthX()).isEqualTo(0.2);
            assertThat(boundingBox.height()).isEqualTo(0.2);
            assertThat(boundingBox.widthZ()).isEqualTo(0.2);
        });

        assertThat(result).isEqualTo(hitbox);
    }

    @Test
    void matchesEntityAlwaysReturnsFalse() {
        Entity entity = mock(Entity.class);

        boolean matches = deploymentObject.matchesEntity(entity);

        assertThat(matches).isFalse();
    }

    @Test
    void removeRemovesBlock() {
        deploymentObject.remove();

        verify(block).setType(Material.AIR);
    }
}
