package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private Equipment equipment;
    private ThrowDeployment deployment;

    @BeforeEach
    public void setUp() {
        equipment = mock(Equipment.class);
        deployment = mock(ThrowDeployment.class);
    }

    @Test
    public void isPerformingReturnsFalseIfEquipmentIsNotPerformingDeployment() {
        when(equipment.isPerformingDeployment()).thenReturn(false);

        ThrowFunction function = new ThrowFunction(equipment, deployment);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void isPerformingReturnsTrueIfEquipmentIsPerformingDeployment() {
        when(equipment.isPerformingDeployment()).thenReturn(true);

        ThrowFunction function = new ThrowFunction(equipment, deployment);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void performCreatesThrowDeployment() {
        DeploymentProperties deploymentProperties = new DeploymentProperties();
        when(equipment.getDeploymentProperties()).thenReturn(deploymentProperties);

        EquipmentHolder holder = mock(EquipmentHolder.class);

        ThrowFunction function = new ThrowFunction(equipment, deployment);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(equipment).performDeployment(deployment, holder);
    }
}
