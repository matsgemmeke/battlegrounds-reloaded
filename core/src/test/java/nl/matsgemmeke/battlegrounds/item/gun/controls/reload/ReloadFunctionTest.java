package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

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
    @InjectMocks
    private ReloadFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isAvailable returns whether gun can perform reload")
    void isAvailable_returnsWhetherGunCanReload(boolean reloadAvailable, boolean expectedAvailable) {
        when(gun.isReloadAvailable()).thenReturn(reloadAvailable);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expectedAvailable);
    }

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
    @DisplayName("performs returns false when gun reload is not available")
    void perform_gunNotAvailable() {
        when(gun.isReloadAvailable()).thenReturn(false);

        GunUser user = mock(GunUser.class);

        boolean performed = function.perform(user);

        assertThat(performed).isFalse();

        verify(gun, never()).reload(any(GunUser.class));
    }

    @Test
    @DisplayName("performs returns true and performs reload")
    void perform_performsReload() {
        GunUser user = mock(GunUser.class);

        when(gun.isReloadAvailable()).thenReturn(true);

        boolean performed = function.perform(user);

        assertThat(performed).isTrue();

        verify(gun).reload(user);
    }
}
