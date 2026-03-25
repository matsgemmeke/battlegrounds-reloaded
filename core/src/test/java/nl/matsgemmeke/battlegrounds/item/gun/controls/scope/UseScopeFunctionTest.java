package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
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
class UseScopeFunctionTest {

    @Mock
    private Gun gun;
    @InjectMocks
    private UseScopeFunction function;

    @ParameterizedTest
    @CsvSource({ "true,false", "false,true" })
    @DisplayName("isAvailable returns whether gun is not using its scope")
    void isAvailable_returnsWhetherGunDoesNotUseScope(boolean gunScoped, boolean expectedAvailable) {
        when(gun.isUsingScope()).thenReturn(gunScoped);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expectedAvailable);
    }

    @Test
    @DisplayName("cancel cancels gun scope")
    void cancel_cancelsScope() {
        when(gun.cancelScope()).thenReturn(true);

        boolean cancelled = function.cancel();

        assertThat(cancelled).isTrue();

        verify(gun).cancelScope();
    }

    @Test
    @DisplayName("perform returns false when gun is using scope")
    void perform_gunUsesScope() {
        GunUser user = mock(GunUser.class);

        when(gun.isUsingScope()).thenReturn(true);

        boolean performed = function.perform(user);

        assertThat(performed).isFalse();

        verify(gun, never()).applyScope(any(ScopeUser.class));
    }

    @Test
    @DisplayName("perform returns true and applies scope")
    void perform_appliesScope() {
        GunUser user = mock(GunUser.class);

        when(gun.applyScope(user)).thenReturn(true);
        when(gun.isUsingScope()).thenReturn(false);

        boolean performed = function.perform(user);

        assertThat(performed).isTrue();

        verify(gun).applyScope(user);
    }
}
