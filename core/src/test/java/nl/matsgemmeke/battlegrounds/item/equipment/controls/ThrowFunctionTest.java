package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
    public void isAvailableReturnsTrueIfEquipmentIsNotDeployed() {
        when(equipment.isDeployed()).thenReturn(false);

        ThrowFunction function = new ThrowFunction(equipment, deployment);
        boolean available = function.isAvailable();

        assertThat(available).isTrue();
    }

    @Test
    public void isAvailableReturnsTrueIfEquipmentIsAlreadyDeployed() {
        when(equipment.isDeployed()).thenReturn(true);

        ThrowFunction function = new ThrowFunction(equipment, deployment);
        boolean available = function.isAvailable();

        assertThat(available).isFalse();
    }

    @Test
    public void performCreatesThrowDeployment() {
        DeploymentProperties deploymentProperties = new DeploymentProperties();
        when(equipment.getDeploymentProperties()).thenReturn(deploymentProperties);

        EquipmentHolder holder = mock(EquipmentHolder.class);

        ThrowFunction function = new ThrowFunction(equipment, deployment);
        boolean performed = function.perform(holder);

        assertThat(performed).isTrue();

        verify(equipment).performDeployment(deployment, holder);
    }
}
