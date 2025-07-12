package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.item.DamageDistanceSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeProfileMapperTest {

    @Test
    public void mapReturnsRangeProfileContrainingValuesFromGivenSpec() {
        DamageDistanceSpec shortRangeSpec = new DamageDistanceSpec();
        shortRangeSpec.damage = 10.0;
        shortRangeSpec.distance = 3.0;

        DamageDistanceSpec mediumRangeSpec = new DamageDistanceSpec();
        mediumRangeSpec.damage = 20.0;
        mediumRangeSpec.distance = 2.0;

        DamageDistanceSpec longRangeSpec = new DamageDistanceSpec();
        longRangeSpec.damage = 10.0;
        longRangeSpec.distance = 3.0;

        RangeProfileSpec spec = new RangeProfileSpec();
        spec.shortRange = shortRangeSpec;
        spec.mediumRange = mediumRangeSpec;
        spec.longRange = longRangeSpec;

        RangeProfileMapper mapper = new RangeProfileMapper();
        RangeProfile rangeProfile = mapper.map(spec);

        assertThat(rangeProfile.shortRangeDamage()).isEqualTo(shortRangeSpec.damage);
        assertThat(rangeProfile.shortRangeDistance()).isEqualTo(shortRangeSpec.distance);
        assertThat(rangeProfile.mediumRangeDamage()).isEqualTo(mediumRangeSpec.damage);
        assertThat(rangeProfile.mediumRangeDistance()).isEqualTo(mediumRangeSpec.distance);
        assertThat(rangeProfile.longRangeDamage()).isEqualTo(longRangeSpec.damage);
        assertThat(rangeProfile.longRangeDistance()).isEqualTo(longRangeSpec.distance);
    }
}
