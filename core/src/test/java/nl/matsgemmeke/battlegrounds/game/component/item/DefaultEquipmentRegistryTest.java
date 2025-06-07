package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class DefaultEquipmentRegistryTest {

    private ItemContainer<Equipment, EquipmentHolder> equipmentContainer;

    @BeforeEach
    public void setUp() {
        equipmentContainer = new ItemContainer<>();
    }

    @Test
    public void findAllReturnsAllEquipmentItemsFromContainer() {
        Equipment equipment = mock(Equipment.class);
        equipmentContainer.addUnassignedItem(equipment);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentContainer);
        List<Equipment> equipmentList = equipmentRegistry.findAll();

        assertEquals(1, equipmentList.size());
        assertEquals(equipment, equipmentList.get(0));
    }

    @Test
    public void getAssignedItemsReturnsAssignedItemsFromContainer() {
        Equipment equipment = mock(Equipment.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        equipmentContainer.addAssignedItem(equipment, holder);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentContainer);
        List<Equipment> assignedItems = equipmentRegistry.getAssignedItems(holder);

        assertThat(assignedItems).containsExactly(equipment);
    }

    @Test
    public void registerItemRegistersUnassignedEquipmentToContainer() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentContainer);
        equipmentRegistry.registerItem(equipment);

        assertEquals(equipment, equipmentContainer.getUnassignedItem(itemStack));
    }

    @Test
    public void registerItemRegistersAssignedEquipmentToContainer() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentContainer);
        equipmentRegistry.registerItem(equipment, holder);

        assertEquals(equipment, equipmentContainer.getAssignedItem(holder, itemStack));
    }

    @Test
    public void unassignItemDoesNothingIfGivenEquipmentHasNoHolder() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(null);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentContainer);
        equipmentRegistry.unassignItem(equipment);

        assertNull(equipmentContainer.getUnassignedItem(itemStack));
    }

    @Test
    public void unassignItemRemovesEquipmentFromAssignedListAndAddsEquipmentToUnassignedList() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(holder);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        equipmentContainer.addAssignedItem(equipment, holder);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(equipmentContainer);
        equipmentRegistry.unassignItem(equipment);

        assertNull(equipmentContainer.getAssignedItem(holder, itemStack));
        assertEquals(equipment, equipmentContainer.getUnassignedItem(itemStack));
    }
}
