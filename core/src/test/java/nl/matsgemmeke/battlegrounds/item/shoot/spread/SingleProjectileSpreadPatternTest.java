package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SingleProjectileSpreadPatternTest {

    @Test
    public void getShotDirectionsReturnsWhichOnlyContainsGivenShootDirection() {
        Location shootDirection = new Location(null, 1, 1, 1, 90.0f, 0.0f);

        SingleProjectileSpreadPattern spreadPattern = new SingleProjectileSpreadPattern();
        List<Location> shootDirections = spreadPattern.getShotDirections(shootDirection);

        assertThat(shootDirections).containsExactly(shootDirection);
    }
}
