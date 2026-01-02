package nl.matsgemmeke.battlegrounds.game.component.targeting.condition;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HitboxTargetConditionTest {

    private static final Location LOCATION = new Location(null, 1, 1, 1);

    private HitboxTargetCondition condition;

    @BeforeEach
    void setUp() {
        condition = new HitboxTargetCondition();
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    void testReturnsWhetherTargetHitboxIntersectsGivenLocation(boolean intersects, boolean expectedResult) {
        DamageTarget damageTarget = mock(DamageTarget.class, Mockito.RETURNS_DEEP_STUBS);
        when(damageTarget.getHitbox().intersects(LOCATION)).thenReturn(intersects);

        boolean result = condition.test(damageTarget, LOCATION);

        assertThat(result).isEqualTo(expectedResult);
    }
}
