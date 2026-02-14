package nl.matsgemmeke.battlegrounds.item.melee.controls.reload;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
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
    @InjectMocks
    private ReloadFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isAvailable returns whether melee weapon is able to reload")
    void isAvailable_returnsWhetherMeleeWeaponCanReload(boolean reloadSystemAvailable, boolean expected) {
        when(meleeWeapon.isReloadAvailable()).thenReturn(reloadSystemAvailable);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expected);
    }

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
    @DisplayName("perform does not perform reload when melee weapon cannot reload")
    void perform_reloadNotAvailable() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);

        when(meleeWeapon.isReloadAvailable()).thenReturn(false);

        function.perform(holder);

        verify(meleeWeapon, never()).reload(any(ReloadPerformer.class));
    }

    @Test
    @DisplayName("perform performs reload when melee weapon can reload")
    void perform_reloadAvailable() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);

        when(meleeWeapon.isReloadAvailable()).thenReturn(true);

        function.perform(holder);

        verify(meleeWeapon).reload(holder);
    }
}
