package nl.matsgemmeke.battlegrounds.game.component.targeting.area;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointTargetAreaTest {

    private static final Location LOCATION = new Location(null, 1, 1, 1);

    private PointTargetArea targetArea;

    @BeforeEach
    void setUp() {
        targetArea = new PointTargetArea();
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    void containsReturnsWhetherTargetHitboxIntersectsGivenLocation(boolean intersects, boolean shouldContain) {
        DamageTarget damageTarget = mock(DamageTarget.class, Mockito.RETURNS_DEEP_STUBS);
        when(damageTarget.getHitbox().intersects(LOCATION)).thenReturn(intersects);

        boolean contains = targetArea.contains(damageTarget, LOCATION);

        assertThat(contains).isEqualTo(shouldContain);
    }
}
