package nl.matsgemmeke.battlegrounds.item.equipment.controls.place;

import nl.matsgemmeke.battlegrounds.item.deploy.action.PlaceDeploymentAction;
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
class PlaceFunctionTest {

    @Mock
    private Equipment equipment;
    @Mock
    private PlaceDeploymentAction deploymentAction;
    @InjectMocks
    private PlaceFunction function;

    @ParameterizedTest
    @CsvSource({ "true,false", "false,true" })
    @DisplayName("isAvailable returns whether equipment is not deployed")
    void isAvailable_returnsWhetherEquipmentNotDeployed(boolean deployed, boolean expectedAvailable) {
        when(equipment.isDeployed()).thenReturn(deployed);

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
    @DisplayName("perform returns true and performs place deployment")
    void perform_performsDeployment() {
        EquipmentUser user = mock(EquipmentUser.class);
        when(user.canDeploy()).thenReturn(true);

        boolean performed = function.perform(user);

        assertThat(performed).isTrue();

        verify(equipment).performDeploymentAction(deploymentAction, user);
    }
}
