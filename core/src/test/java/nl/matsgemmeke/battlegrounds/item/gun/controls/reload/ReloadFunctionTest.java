package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
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
    private Gun gun;
    @Mock
    private GunUser user;
    @InjectMocks
    private ReloadFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isPerforming returns whether gun is reloading")
    void isPerforming_returnsWhetherGunIsReloading(boolean reloading, boolean expectedPerforming) {
        when(gun.isReloading()).thenReturn(reloading);

        boolean performing = function.isPerforming();

        assertThat(performing).isEqualTo(expectedPerforming);
    }

    @Test
    @DisplayName("cancel cancels gun reload operation")
    void cancel_cancelsReload() {
        when(gun.cancelReload()).thenReturn(true);

        boolean cancelled = function.cancel();

        assertThat(cancelled).isTrue();

        verify(gun).cancelReload();
    }

    @Test
    @DisplayName("performs returns DENIED when gun reload is not available")
    void perform_gunNotAvailable() {
        when(gun.isReloadAvailable()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.DENIED);

        verify(gun, never()).reload(any(GunUser.class));
    }

    @Test
    @DisplayName("performs returns SUCCESS and performs reload")
    void perform_performsReload() {
        when(gun.isReloadAvailable()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(gun).reload(user);
    }
}
