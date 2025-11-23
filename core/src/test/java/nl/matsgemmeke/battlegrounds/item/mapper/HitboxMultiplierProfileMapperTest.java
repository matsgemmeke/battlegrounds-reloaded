package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.item.HitboxMultiplierSpec;
import nl.matsgemmeke.battlegrounds.item.effect.damage.HitboxMultiplierProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HitboxMultiplierProfileMapperTest {

    private static final double HEADSHOT_DAMAGE_MULTIPLIER = 1.5;
    private static final double BODY_DAMAGE_MULTIPLIER = 1.0;
    private static final double LEGS_DAMAGE_MULTIPLIER = 0.5;

    @Test
    void mapReturnsHitboxMultiplierProfileWithValuesFromSpec() {
        HitboxMultiplierSpec spec = new HitboxMultiplierSpec();
        spec.head = HEADSHOT_DAMAGE_MULTIPLIER;
        spec.body = BODY_DAMAGE_MULTIPLIER;
        spec.legs = LEGS_DAMAGE_MULTIPLIER;

        HitboxMultiplierProfileMapper mapper = new HitboxMultiplierProfileMapper();
        HitboxMultiplierProfile hitboxMultiplierProfile = mapper.map(spec);

        assertThat(hitboxMultiplierProfile.headshotDamageMultiplier()).isEqualTo(HEADSHOT_DAMAGE_MULTIPLIER);
        assertThat(hitboxMultiplierProfile.bodyDamageMultiplier()).isEqualTo(BODY_DAMAGE_MULTIPLIER);
        assertThat(hitboxMultiplierProfile.legsDamageMultiplier()).isEqualTo(LEGS_DAMAGE_MULTIPLIER);
    }
}
