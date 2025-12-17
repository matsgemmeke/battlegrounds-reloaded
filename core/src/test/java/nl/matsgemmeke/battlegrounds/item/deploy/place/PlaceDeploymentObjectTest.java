package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
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
    private HitboxProvider<StaticBoundingBox> hitboxProvider;

    private PlaceDeploymentObject deploymentObject;

    @BeforeEach
    void setUp() {
        deploymentObject = new PlaceDeploymentObject(block, MATERIAL, hitboxProvider);
    }

    @ParameterizedTest
    @CsvSource({ "WARPED_BUTTON,true", "STONE,false" })
    public void existsReturnsWhetherBlockTypeEqualsOriginalMaterial(Material material, boolean expectedResult) {
        when(block.getType()).thenReturn(material);

        boolean exists = deploymentObject.exists();

        assertThat(exists).isEqualTo(expectedResult);
    }

    @Test
    void getLastDamageReturnsNullWhenBlockHasNotTakenDamage() {
        Damage lastDamage = deploymentObject.getLastDamage();

        assertThat(lastDamage).isNull();
    }

    @Test
    void getLastDamageReturnsLastDamageDealtToBlock() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        deploymentObject.damage(damage);
        Damage lastDamage = deploymentObject.getLastDamage();

        assertThat(lastDamage).isEqualTo(damage);
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
                arguments(10.0, 10.0, 100.0, 90.0, DamageType.BULLET_DAMAGE, Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.5)),
                arguments(1000.0, 1000.0, 100.0, 0.0, DamageType.BULLET_DAMAGE, null)
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
    void getNameReturnsClassName() {
        String name = deploymentObject.getName();

        assertThat(name).isEqualTo("PlaceDeploymentObject");
    }

    @Test
    void isImmuneReturnsFalseWhenResistancesIsNull() {
        boolean immune = deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertThat(immune).isFalse();
    }

    @Test
    void isImmuneReturnsFalseWhenResistancesDoesNotContainEntryForDamageType() {
        Map<DamageType, Double> resistances = Map.of(DamageType.EXPLOSIVE_DAMAGE, 0.0);

        deploymentObject.setResistances(resistances);
        boolean immune = deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertThat(immune).isFalse();
    }

    @Test
    void isImmuneReturnsFalseWhenResistanceToDamageTypeIsLargerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.5);

        deploymentObject.setResistances(resistances);
        boolean immune = deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertThat(immune).isFalse();
    }

    @Test
    void isImmuneReturnsTrueWhenResistanceToDamageTypeEqualsOrIsLowerThanZero() {
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        deploymentObject.setResistances(resistances);
        boolean immune = deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE);

        assertThat(immune).isTrue();
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
