package nl.matsgemmeke.battlegrounds.game.component.targeting.condition;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProximityTargetConditionTest {

    private static final double MAX_DISTANCE = 1.0;

    private ProximityTargetCondition condition;

    @BeforeEach
    void setUp() {
        condition = new ProximityTargetCondition(MAX_DISTANCE);
    }

    @ParameterizedTest
    @CsvSource({ "1.5,1.5,1.5,true", "5.0,5.0,5.0,false" })
    void testReturnsWhetherDamageTargetDistanceFromLocationIsSmallerOrEqualToRadius(double originX, double originY, double originZ, boolean expectedResult) {
        World world = mock(World.class);
        Location damageTargetLocation = new Location(world, 1, 1, 1);
        Location origin = new Location(world, originX, originY, originZ);

        DamageTarget damageTarget = mock(DamageTarget.class);
        when(damageTarget.getLocation()).thenReturn(damageTargetLocation);

        boolean result = condition.test(damageTarget, origin);

        assertThat(result).isEqualTo(expectedResult);
    }
}
