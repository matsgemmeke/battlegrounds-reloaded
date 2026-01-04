package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.damage.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultExplosionDamageModifierTest {

    private static final double DAMAGE = 100.0;

    @Mock
    private DamageSource source;
    @Mock
    private DamageTarget target;
    @InjectMocks
    private DefaultExplosionDamageModifier modifier;

    @ParameterizedTest
    @CsvSource({ "BULLET_DAMAGE,100.0", "EXPLOSIVE_DAMAGE,0.0" })
    void applyReturnsDamageContextWithModifiedDamageDependingOnDamageType(DamageType damageType, double expectedDamageAmount) {
        Damage damage = new Damage(DAMAGE, damageType);
        DamageContext damageContext = new DamageContext(source, target, damage);

        DamageContext result = modifier.apply(damageContext);

        assertThat(result.damage().amount()).isEqualTo(expectedDamageAmount);
    }
}
