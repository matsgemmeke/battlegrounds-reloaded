package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.deploy.drop.DropDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
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
class DropFunctionTest {

    @Mock
    private Equipment equipment;
    @Mock
    private DropDeployment deployment;
    @InjectMocks
    private DropFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isAvailable returns whether equipment is deployed")
    void isAvailable_returnsEquipmentDeployed(boolean deployed, boolean expectedAvailable) {
        when(equipment.isDeployed()).thenReturn(deployed);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expectedAvailable);
    }

    @Test
    @DisplayName("perform returns false when holder cannot deploy")
    void perform_holderCannotDeploy() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.canDeploy()).thenReturn(false);

        boolean performed = function.perform(holder);

        assertThat(performed).isFalse();

        verifyNoInteractions(equipment);
    }

    @Test
    @DisplayName("perform returns true and performs deployment on equipment")
    void perform_performsDeployment() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.canDeploy()).thenReturn(true);

        boolean performed = function.perform(holder);

        assertThat(performed).isTrue();

        verify(equipment).performDeployment(deployment, holder);
    }
}
