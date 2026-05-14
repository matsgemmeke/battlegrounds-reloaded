package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UseScopeFunctionTest {

    @Mock
    private Gun gun;
    @Mock
    private GunUser user;
    @InjectMocks
    private UseScopeFunction function;

    @Test
    @DisplayName("cancel cancels gun scope")
    void cancel_cancelsScope() {
        when(gun.cancelScope()).thenReturn(true);

        boolean cancelled = function.cancel();

        assertThat(cancelled).isTrue();

        verify(gun).cancelScope();
    }

    @Test
    @DisplayName("perform returns FAILED when gun is using scope")
    void perform_gunUsesScope() {
        when(gun.isUsingScope()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.FAILED);

        verify(gun, never()).applyScope(any(ScopeUser.class));
    }

    @Test
    @DisplayName("perform returns SUCCESS and applies scope")
    void perform_appliesScope() {
        when(gun.applyScope(user)).thenReturn(true);
        when(gun.isUsingScope()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(gun).applyScope(user);
    }
}
