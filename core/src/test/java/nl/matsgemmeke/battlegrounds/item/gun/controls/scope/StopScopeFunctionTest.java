package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StopScopeFunctionTest {

    @Mock
    private Gun gun;
    @Mock
    private GunUser user;
    @InjectMocks
    private StopScopeFunction function;

    @Test
    @DisplayName("perform returns SUCCESS and cancels scope")
    void perform_gunUsingScope() {
        when(gun.isUsingScope()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(gun).cancelScope();
    }

    @Test
    @DisplayName("perform returns FAILED when gun is not using scope")
    void perform_gunNotUsingScope() {
        when(gun.isUsingScope()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.FAILED);

        verify(gun, never()).cancelScope();
    }
}
