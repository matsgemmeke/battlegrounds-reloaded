package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import com.google.common.collect.Iterators;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuckshotSpreadPatternTest {

    @Test
    public void shouldReturnAmountOfLocationsBased() {
        int pelletAmount = 5;

        Location location = new Location(null, 0, 0, 0);

        BuckshotSpreadPattern pattern = new BuckshotSpreadPattern(pelletAmount, 1.0f, 1.0f);
        Iterable<Location> directions = pattern.getShootingDirections(location);

        assertEquals(pelletAmount, Iterators.size(directions.iterator()));
    }

    @Test
    public void shouldReturnLocationsWithDeviatedPitchAndYaw() {
        Location location = new Location(null, 0, 0, 0, 10.0f, 10.0f);

        BuckshotSpreadPattern pattern = new BuckshotSpreadPattern(10, 10.0f, 10.0f);
        Iterable<Location> directions = pattern.getShootingDirections(location);

        for (Location direction : directions) {
            assertTrue(direction.getYaw() >= 0.0f);
            assertTrue(direction.getYaw() <= 20.0f);
            assertTrue(direction.getPitch() >= 0.0f);
            assertTrue(direction.getPitch() <= 20.0f);
        }
    }
}
