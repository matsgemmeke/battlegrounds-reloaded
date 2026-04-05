package nl.matsgemmeke.battlegrounds.item.equipment.controls.cook;

import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeploymentAction;
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
class CookFunctionTest {

    @Mock
    private Equipment equipment;
    @Mock
    private PrimeDeploymentAction deploymentAction;
    @InjectMocks
    private CookFunction function;

    @ParameterizedTest
    @CsvSource({ "true,false", "false,true" })
    @DisplayName("isAvailable returns whether equipment is not awaiting deployment")
    void isAvailable_returnsEquipmentNotAwaitsDeployment(boolean awaitingDeployment, boolean expectedAvailable) {
        when(equipment.isAwaitingDeployment()).thenReturn(awaitingDeployment);

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
    @DisplayName("perform returns true and starts deployment process")
    void perform_startsDeploymentProcess() {
        EquipmentUser user = mock(EquipmentUser.class);
        when(user.canDeploy()).thenReturn(true);

        boolean performed = function.perform(user);

        assertThat(performed).isTrue();

        verify(equipment).performDeploymentAction(deploymentAction, user);
    }
}
