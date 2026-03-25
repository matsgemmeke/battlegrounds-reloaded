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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StopScopeFunctionTest {

    @Mock
    private Gun gun;
    @InjectMocks
    private StopScopeFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isAvailable returns whether gun is scoped")
    void isAvailable_returnsWhetherGunScoped(boolean scoped, boolean expectedAvailable) {
        when(gun.isUsingScope()).thenReturn(scoped);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expectedAvailable);
    }

    @Test
    @DisplayName("perform returns true and cancels scope")
    void perform_gunUsingScope() {
        GunUser user = mock(GunUser.class);

        when(gun.isUsingScope()).thenReturn(true);

        boolean performed = function.perform(user);

        assertTrue(performed);

        verify(gun).cancelScope();
    }

    @Test
    @DisplayName("perform returns false when gun is not using scope")
    void perform_gunNotUsingScope() {
        GunUser user = mock(GunUser.class);

        when(gun.isUsingScope()).thenReturn(false);

        boolean performed = function.perform(user);

        assertFalse(performed);

        verify(gun, never()).cancelScope();
    }
}
