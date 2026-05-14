package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.deploy.action.ThrowDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrowFunctionTest {

    @Mock
    private Equipment equipment;
    @Mock
    private EquipmentUser user;
    @Mock
    private ThrowDeploymentAction deploymentAction;
    @InjectMocks
    private ThrowFunction function;

    @Test
    @DisplayName("perform returns FAILED when equipment is already deployed")
    void perform_equipmentNotDeployed() {
        when(equipment.isDeployed()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.FAILED);
    }

    @Test
    @DisplayName("perform returns FAILED when user cannot deploy")
    void perform_userCannotDeploy() {
        when(equipment.isDeployed()).thenReturn(false);
        when(user.canDeploy()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.FAILED);
    }

    @Test
    @DisplayName("perform returns true and performs throw deployment")
    void perform_performsThrowDeployment() {
        when(equipment.isDeployed()).thenReturn(false);
        when(user.canDeploy()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(equipment).performDeploymentAction(deploymentAction, user);
    }
}
