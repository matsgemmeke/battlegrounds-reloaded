package nl.matsgemmeke.battlegrounds.game.component.deploy;

import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultDeploymentInfoProviderTest {

    private EquipmentRegistry equipmentRegistry;

    @BeforeEach
    public void setUp() {
        equipmentRegistry = mock(EquipmentRegistry.class);
    }

    @Test
    public void getAllDeploymentObjectsReturnsAllDeploymentObjectsFromEveryDeployableItemInTheContext() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getDeploymentObject()).thenReturn(deploymentObject);

        when(equipmentRegistry.getAllEquipment()).thenReturn(List.of(equipment));

        DefaultDeploymentInfoProvider deploymentInfoProvider = new DefaultDeploymentInfoProvider(equipmentRegistry);
        List<DeploymentObject> deploymentObjects = deploymentInfoProvider.getAllDeploymentObjects();

        assertEquals(1, deploymentObjects.size());
        assertEquals(deploymentObject, deploymentObjects.get(0));
    }

    @Test
    public void getDeployableItemReturnsEquipmentItemIfFoundInstanceContainsGivenDeploymentObject() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getDeploymentObject()).thenReturn(deploymentObject);

        when(equipmentRegistry.getAllEquipment()).thenReturn(List.of(equipment));

        DefaultDeploymentInfoProvider deploymentInfoProvider = new DefaultDeploymentInfoProvider(equipmentRegistry);
        DeployableItem deployableItem = deploymentInfoProvider.getDeployableItem(deploymentObject);

        assertEquals(deployableItem, equipment);
    }

    @Test
    public void getDeployableItemReturnsNullIfNoneOfTheDeployableItemsContainsGivenDeploymentObject() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        DeploymentObject otherDeploymentObject = mock(DeploymentObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getDeploymentObject()).thenReturn(deploymentObject);

        when(equipmentRegistry.getAllEquipment()).thenReturn(List.of(equipment));

        DefaultDeploymentInfoProvider deploymentInfoProvider = new DefaultDeploymentInfoProvider(equipmentRegistry);
        DeployableItem deployableItem = deploymentInfoProvider.getDeployableItem(otherDeploymentObject);

        assertNull(deployableItem);
    }
}
