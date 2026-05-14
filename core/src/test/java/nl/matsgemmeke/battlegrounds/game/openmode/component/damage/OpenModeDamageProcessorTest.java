package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModeDamageProcessorTest {

    @Mock
    private GameKey gameKey;
    @InjectMocks
    private OpenModeDamageProcessor damageProcessor;

    @Test
    void isDamageAllowedReturnsFalseWhenGivenGameKeyIsDifferent() {
        GameKey otherGameKey = GameKey.ofSession(1);

        boolean allowed = damageProcessor.isDamageAllowed(otherGameKey);

        assertFalse(allowed);
    }

    @Test
    void isDamageAllowedReturnsTrueWhenGivenGameKeyIsSame() {
        boolean allowed = damageProcessor.isDamageAllowed(gameKey);

        assertTrue(allowed);
    }

    @Test
    void isDamageAllowedWithoutContextAlwaysReturnTrue() {
        boolean allowed = damageProcessor.isDamageAllowedWithoutContext();

        assertThat(allowed).isTrue();
    }

    @Test
    void processDamageAppliesDamageModifiersAndDamagesTarget() {
        DamageSource source = mock(DamageSource.class);
        DamageTarget target = mock(DamageTarget.class);
        Damage originalDamage = new Damage(10.0, DamageType.BULLET_DAMAGE);
        Damage modifiedDamage = new Damage(20.0, DamageType.BULLET_DAMAGE);

        DamageContext originalDamageContext = new DamageContext(source, target, originalDamage);
        DamageContext modifiedDamageContext = new DamageContext(source, target, modifiedDamage);

        DamageModifier damageModifier = mock(DamageModifier.class);
        when(damageModifier.apply(originalDamageContext)).thenReturn(modifiedDamageContext);

        damageProcessor.addDamageModifier(damageModifier);
        damageProcessor.processDamage(originalDamageContext);

        verify(target).damage(modifiedDamage);
    }
}
