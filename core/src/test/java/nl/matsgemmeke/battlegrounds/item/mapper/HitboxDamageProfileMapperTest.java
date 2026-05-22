package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.item.HitboxDamageProfileSpec;
import nl.matsgemmeke.battlegrounds.item.effect.damage.HitboxDamageProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HitboxDamageProfileMapperTest {

    private static final double HEAD_DAMAGE_MODIFIER = 1.5;
    private static final double TORSO_DAMAGE_MODIFIER = 1.0;
    private static final double LIMBS_DAMAGE_MODIFIER = 0.5;

    private final HitboxDamageProfileMapper mapper = new HitboxDamageProfileMapper();

    @Test
    @DisplayName("map returns HitboxDamageProfile with values from given spec")
    void map() {
        HitboxDamageProfileSpec spec = new HitboxDamageProfileSpec();
        spec.head = HEAD_DAMAGE_MODIFIER;
        spec.torso = TORSO_DAMAGE_MODIFIER;
        spec.limbs = LIMBS_DAMAGE_MODIFIER;

        HitboxDamageProfile hitboxDamageProfile = mapper.map(spec);

        assertThat(hitboxDamageProfile.headDamageModifier()).isEqualTo(HEAD_DAMAGE_MODIFIER);
        assertThat(hitboxDamageProfile.torsoDamageModifier()).isEqualTo(TORSO_DAMAGE_MODIFIER);
        assertThat(hitboxDamageProfile.limbsDamageMultiplier()).isEqualTo(LIMBS_DAMAGE_MODIFIER);
    }
}
