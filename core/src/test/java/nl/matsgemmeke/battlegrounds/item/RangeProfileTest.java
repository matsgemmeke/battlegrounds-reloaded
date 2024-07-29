package nl.matsgemmeke.battlegrounds.item;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RangeProfileTest {

    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;

    @Test
    public void shouldReturnShortDamageForShortDistance() {
        RangeProfile rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        double distance = 1.0;

        double result = rangeProfile.getDamageByDistance(distance);

        assertEquals(SHORT_RANGE_DAMAGE, result, 0.0);
    }

    @Test
    public void shouldReturnMediumDamageForMediumDistance() {
        RangeProfile rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        double distance = 4.0;

        double result = rangeProfile.getDamageByDistance(distance);

        assertEquals(MEDIUM_RANGE_DAMAGE, result, 0.0);
    }

    @Test
    public void shouldReturnLongDamageForLongDistance() {
        RangeProfile rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        double distance = 8.0;

        double result = rangeProfile.getDamageByDistance(distance);

        assertEquals(LONG_RANGE_DAMAGE, result, 0.0);
    }

    @Test
    public void shouldReturnZeroDamageForDistancesOutsideOfLongRange() {
        RangeProfile rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        double distance = 100.0;

        double result = rangeProfile.getDamageByDistance(distance);

        assertEquals(0.0, result, 0.0);
    }
}
