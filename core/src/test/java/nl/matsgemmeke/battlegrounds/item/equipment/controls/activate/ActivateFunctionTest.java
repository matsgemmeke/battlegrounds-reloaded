package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateFunctionTest {

    @Mock
    private Equipment equipment;
    @Mock
    private EquipmentUser user;
    @InjectMocks
    private ActivateFunction function;

    @Test
    @DisplayName("perform returns DENIED when equipment activator is not ready")
    void perform_equipmentNotReady() {
        when(equipment.isActivatorReady()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.DENIED);
    }

    @Test
    @DisplayName("perform returns DENIED when user cannot deploy")
    void perform_userCannotDeploy() {
        when(equipment.isActivatorReady()).thenReturn(true);
        when(user.canDeploy()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.DENIED);
    }

    @Test
    @DisplayName("perform returns SUCCESS and activates deployment")
    void perform_activatesDeployment() {
        when(equipment.isActivatorReady()).thenReturn(true);
        when(user.canDeploy()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(equipment).activateDeployment(user);
    }
}
