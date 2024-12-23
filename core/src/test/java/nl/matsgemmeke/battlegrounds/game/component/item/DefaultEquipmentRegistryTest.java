package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class DefaultEquipmentRegistryTest {

    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;

    @BeforeEach
    public void setUp() {
        equipmentStorage = new ItemStorage<>();
    }

    @Test
    public void registerItemRegistersUnassignedEquipmentToStorage() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentStorage);
        equipmentRegistry.registerItem(equipment);

        assertEquals(equipment, equipmentStorage.getUnassignedItem(itemStack));
    }

    @Test
    public void registerItemRegistersAssignedEquipmentToStorage() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentStorage);
        equipmentRegistry.registerItem(equipment, holder);

        assertEquals(equipment, equipmentStorage.getAssignedItem(holder, itemStack));
    }

    @Test
    public void unassignItemDoesNothingIfGivenEquipmentHasNoHolder() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(null);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentStorage);
        equipmentRegistry.unassignItem(equipment);

        assertNull(equipmentStorage.getUnassignedItem(itemStack));
    }

    @Test
    public void unassignItemRemovesEquipmentFromAssignedListAndAddsEquipmentToUnassignedList() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(holder);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        equipmentStorage.addAssignedItem(equipment, holder);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentStorage);
        equipmentRegistry.unassignItem(equipment);

        assertNull(equipmentStorage.getAssignedItem(holder, itemStack));
        assertEquals(equipment, equipmentStorage.getUnassignedItem(itemStack));
    }
}
