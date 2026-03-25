package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

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
class ChangeScopeMagnificationFunctionTest {

    @Mock
    private Gun gun;
    @InjectMocks
    private ChangeScopeMagnificationFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isAvailable returns whether gun is scoped")
    void isAvailable_returnsWhetherGunScoped(boolean scoped, boolean expectedAvailable) {
        when(gun.isUsingScope()).thenReturn(scoped);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expectedAvailable);
    }

    @Test
    @DisplayName("perform returns false when gun is not scoped")
    void perform_gunNotScoped() {
        GunUser user = mock(GunUser.class);

        when(gun.isUsingScope()).thenReturn(false);

        boolean performed = function.perform(user);

        assertThat(performed).isFalse();

        verify(gun, never()).changeScopeMagnification();
    }

    @Test
    @DisplayName("perform returns true and changes scope magnification when gun is scoped")
    void perform_gunScoped() {
        GunUser user = mock(GunUser.class);

        when(gun.isUsingScope()).thenReturn(true);

        boolean performed = function.perform(user);

        assertThat(performed).isTrue();

        verify(gun).changeScopeMagnification();
    }
}
