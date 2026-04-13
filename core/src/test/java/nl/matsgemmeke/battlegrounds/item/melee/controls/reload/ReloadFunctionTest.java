package nl.matsgemmeke.battlegrounds.item.melee.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReloadFunctionTest {

    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private MeleeWeaponUser user;
    @InjectMocks
    private ReloadFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isPerforming returns whether melee weapon is reloading")
    void isPerforming_returnsWhetherMeleeWeaponIsReloading(boolean reloading, boolean expected) {
        when(meleeWeapon.isReloading()).thenReturn(reloading);

        boolean performing = function.isPerforming();

        assertThat(performing).isEqualTo(expected);
    }

    @Test
    @DisplayName("cancel cancels reload on melee weapon")
    void cancel_cancelsReload() {
        when(meleeWeapon.cancelReload()).thenReturn(true);

        boolean result = function.cancel();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("perform returns DENIED when melee weapon cannot reload")
    void perform_reloadNotAvailable() {
        when(meleeWeapon.isReloadAvailable()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.DENIED);

        verify(meleeWeapon, never()).reload(any(ReloadPerformer.class));
    }

    @Test
    @DisplayName("perform returns SUCCESS and performs reload")
    void perform_reloadAvailable() {
        when(meleeWeapon.isReloadAvailable()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(meleeWeapon).reload(user);
    }
}
