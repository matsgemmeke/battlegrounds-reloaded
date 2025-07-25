package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BuckshotSpreadPatternTest {

    @Test
    public void getShotDirectionsReturnsLocationsWithDeviatedPitchAndYaw() {
        int pelletAmount = 10;
        Location location = new Location(null, 0, 0, 0, 10.0f, 10.0f);

        BuckshotSpreadPattern pattern = new BuckshotSpreadPattern(pelletAmount, 10.0f, 10.0f);
        Iterable<Location> directions = pattern.getShotDirections(location);

        assertThat(directions).hasSize(pelletAmount);

        for (Location direction : directions) {
            assertThat(direction.getYaw()).isBetween(0.0f, 20.0f);
            assertThat(direction.getPitch()).isBetween(0.0f, 20.0f);
        }
    }
}
