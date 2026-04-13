package nl.matsgemmeke.battlegrounds.item.equipment.controls.cook;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.deploy.action.PrimeDeploymentAction;
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
class CookFunctionTest {

    @Mock
    private Equipment equipment;
    @Mock
    private EquipmentUser user;
    @Mock
    private PrimeDeploymentAction deploymentAction;
    @InjectMocks
    private CookFunction function;

    @Test
    @DisplayName("perform returns DENIED when equipment is not awaiting deployment")
    void perform_equipmentIsNotAwaitingDeployment() {
        when(equipment.isAwaitingDeployment()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.DENIED);
    }

    @Test
    @DisplayName("perform returns DENIED when user cannot deploy")
    void perform_userCannotDeploy() {
        when(equipment.isAwaitingDeployment()).thenReturn(true);
        when(user.canDeploy()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.DENIED);
    }

    @Test
    @DisplayName("perform returns SUCCESS and starts deployment process")
    void perform_startsDeploymentProcess() {
        when(equipment.isAwaitingDeployment()).thenReturn(true);
        when(user.canDeploy()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(equipment).performDeploymentAction(deploymentAction, user);
    }
}
