package nl.matsgemmeke.battlegrounds.item.melee.controls.throwing;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ThrowFunctionTest {

    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private MeleeWeaponUser user;
    @InjectMocks
    private ThrowFunction function;

    @Test
    void isPerformingAlwaysReturnsFalse() {
        boolean performing = function.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void cancelAlwaysReturnsFalse() {
        boolean cancelled = function.cancel();

        assertThat(cancelled).isFalse();
    }

    @Test
    @DisplayName("perform returns SUCCESS and performs throw on melee weapon")
    void perform_performsThrow() {
        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(meleeWeapon).performThrow(user);
    }
}
