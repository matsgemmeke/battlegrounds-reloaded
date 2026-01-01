package nl.matsgemmeke.battlegrounds.game.component.targeting.area;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RadiusTargetAreaTest {

    private static final double RADIUS = 1.0;

    private RadiusTargetArea targetArea;

    @BeforeEach
    void setUp() {
        targetArea = new RadiusTargetArea(RADIUS);
    }

    @ParameterizedTest
    @CsvSource({ "1.5,1.5,1.5,true", "5.0,5.0,5.0,false" })
    void containsReturnsWhetherDamageTargetDistanceFromLocationIsSmallerOrEqualToRadius(double originX, double originY, double originZ, boolean shouldContain) {
        World world = mock(World.class);
        Location damageTargetLocation = new Location(world, 1, 1, 1);
        Location origin = new Location(world, originX, originY, originZ);

        DamageTarget damageTarget = mock(DamageTarget.class);
        when(damageTarget.getLocation()).thenReturn(damageTargetLocation);

        boolean contains = targetArea.contains(damageTarget, origin);

        assertThat(contains).isEqualTo(shouldContain);
    }
}
