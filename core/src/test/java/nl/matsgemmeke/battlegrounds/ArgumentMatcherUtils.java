package nl.matsgemmeke.battlegrounds;

import org.mockito.ArgumentMatcher;

public class ArgumentMatcherUtils {

    public static ArgumentMatcher<Long> isBetween(long min, long max) {
        return duration -> duration != null && duration >= min && duration <= max;
    }
}
