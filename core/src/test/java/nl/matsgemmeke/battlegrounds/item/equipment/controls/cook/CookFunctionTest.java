package nl.matsgemmeke.battlegrounds.item.equipment.controls.cook;

import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CookFunctionTest {

    private Equipment equipment;
    private PrimeDeployment deployment;

    @BeforeEach
    public void setUp() {
        equipment = mock(Equipment.class);
        deployment = mock(PrimeDeployment.class);
    }

    @Test
    public void isAvailableReturnsTrueWhenEquipmentIsNotAwaitingDeployment() {
        when(equipment.isAwaitingDeployment()).thenReturn(false);

        CookFunction function = new CookFunction(equipment, deployment);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isAvailableReturnsFalseWhenEquipmentIsAlreadyAwaitingDeployment() {
        when(equipment.isAwaitingDeployment()).thenReturn(true);

        CookFunction function = new CookFunction(equipment, deployment);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void performReturnsFalseWhenHolderCannotDeploy() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.canDeploy()).thenReturn(false);

        CookFunction function = new CookFunction(equipment, deployment);
        boolean performed = function.perform(holder);

        assertThat(performed).isFalse();

        verifyNoInteractions(equipment);
    }

    @Test
    public void performReturnsTrueAndStartsDeploymentProcess() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.canDeploy()).thenReturn(true);

        CookFunction function = new CookFunction(equipment, deployment);
        boolean performed = function.perform(holder);

        assertThat(performed).isTrue();

        verify(equipment).performDeployment(deployment, holder);
    }
}
