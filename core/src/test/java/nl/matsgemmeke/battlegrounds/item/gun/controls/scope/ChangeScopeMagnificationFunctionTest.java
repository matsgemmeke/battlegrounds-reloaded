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
class ChangeScopeMagnificationFunctionTest {

    @Mock
    private Gun gun;
    @Mock
    private GunUser user;
    @InjectMocks
    private ChangeScopeMagnificationFunction function;

    @Test
    @DisplayName("perform returns FAILED when gun is not scoped")
    void perform_gunNotScoped() {
        when(gun.isUsingScope()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.FAILED);

        verify(gun, never()).changeScopeMagnification();
    }

    @Test
    @DisplayName("perform returns SUCCESS and changes scope magnification")
    void perform_gunScoped() {
        when(gun.isUsingScope()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(gun).changeScopeMagnification();
    }
}
