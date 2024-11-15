package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class DefaultEquipmentRegistryTest {

    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        equipmentStorage = (ItemStorage<Equipment, EquipmentHolder>) mock(ItemStorage.class);
    }

    @Test
    public void shouldRegisterUnassignedItemToStorage() {
        Equipment equipment = mock(Equipment.class);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentStorage);
        equipmentRegistry.registerItem(equipment);

        verify(equipmentStorage).addUnassignedItem(equipment);
    }

    @Test
    public void shouldRegisterAssignedItemToStorage() {
        Equipment equipment = mock(Equipment.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentStorage);
        equipmentRegistry.registerItem(equipment, holder);

        verify(equipmentStorage).addAssignedItem(equipment, holder);
    }
}
