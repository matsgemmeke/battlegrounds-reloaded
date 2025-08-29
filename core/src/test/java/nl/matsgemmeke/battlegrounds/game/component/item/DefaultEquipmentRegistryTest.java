package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DefaultEquipmentRegistryTest {

    @Test
    public void getAllEquipmentReturnsAllEquipmentItems() {
        Equipment equipment = mock(Equipment.class);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry();
        equipmentRegistry.register(equipment);
        List<Equipment> equipmentList = equipmentRegistry.getAllEquipment();

        assertThat(equipmentList).containsExactly(equipment);
    }

    @Test
    public void getAssignedEquipmentReturnsAssignedEquipmentFromGivenHolder() {
        Equipment equipment = mock(Equipment.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry();
        equipmentRegistry.register(equipment, holder);
        List<Equipment> assignedItems = equipmentRegistry.getAssignedEquipment(holder);

        assertThat(assignedItems).containsExactly(equipment);
    }
}
