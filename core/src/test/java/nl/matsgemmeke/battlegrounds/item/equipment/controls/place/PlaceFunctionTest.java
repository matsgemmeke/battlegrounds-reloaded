package nl.matsgemmeke.battlegrounds.item.equipment.controls.place;

import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PlaceFunctionTest {

    private Equipment equipment;
    private PlaceDeployment deployment;

    @BeforeEach
    public void setUp() {
        equipment = mock(Equipment.class);
        deployment = mock(PlaceDeployment.class);
    }

    @Test
    public void isAvailableReturnsFalseWhenEquipmentIsAlreadyDeployed() {
        when(equipment.isDeployed()).thenReturn(true);

        PlaceFunction function = new PlaceFunction(equipment, deployment);
        boolean available = function.isAvailable();

        assertThat(available).isFalse();
    }

    @Test
    public void isPerformingReturnsTrueWhenEquipmentIsNotDeployed() {
        when(equipment.isDeployed()).thenReturn(false);

        PlaceFunction function = new PlaceFunction(equipment, deployment);
        boolean available = function.isAvailable();

        assertThat(available).isTrue();
    }

    @Test
    public void performReturnsTrueAndPerformsDeployment() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        PlaceFunction function = new PlaceFunction(equipment, deployment);
        boolean performed = function.perform(holder);

        assertThat(performed).isTrue();

        verify(equipment).performDeployment(deployment, holder);
    }
}
