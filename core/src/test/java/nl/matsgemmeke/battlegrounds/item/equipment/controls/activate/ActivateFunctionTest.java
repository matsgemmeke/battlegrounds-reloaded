package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
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
class ActivateFunctionTest {

    @Mock
    private Equipment equipment;
    @InjectMocks
    private ActivateFunction function;

    @ParameterizedTest
    @CsvSource({ "false,false", "true,true" })
    @DisplayName("isAvailable returns whether equipment activator is ready")
    void isAvailable_whetherActivatorIsReady(boolean activatorReady, boolean expectedAvailable) {
        when(equipment.isActivatorReady()).thenReturn(activatorReady);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expectedAvailable);
    }

    @Test
    @DisplayName("perform returns false when user cannot deploy")
    void perform_userCannotDeploy() {
        EquipmentUser user = mock(EquipmentUser.class);
        when(user.canDeploy()).thenReturn(false);

        boolean performed = function.perform(user);

        assertThat(performed).isFalse();

        verifyNoInteractions(equipment);
    }

    @Test
    @DisplayName("perform returns true and activates deployment")
    void perform_activatesDeployment() {
        EquipmentUser user = mock(EquipmentUser.class);
        when(user.canDeploy()).thenReturn(true);

        boolean performed = function.perform(user);

        assertThat(performed).isTrue();

        verify(equipment).activateDeployment(user);
    }
}
