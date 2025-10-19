package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;

public class RangeProfileMapper {

    public RangeProfile map(RangeProfileSpec spec) {
        double shortRangeDamage = spec.shortRange.damage;
        double shortRangeDistance = spec.shortRange.distance;
        double mediumRangeDamage = spec.mediumRange.damage;
        double mediumRangeDistance = spec.mediumRange.distance;
        double longRangeDamage = spec.longRange.damage;
        double longRangeDistance = spec.longRange.distance;

        return new RangeProfile(shortRangeDamage, shortRangeDistance, mediumRangeDamage, mediumRangeDistance, longRangeDamage, longRangeDistance);
    }
}
