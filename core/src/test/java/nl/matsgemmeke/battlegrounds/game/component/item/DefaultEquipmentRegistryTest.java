package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
    public void getAssignedEquipmentReturnsEmptyListWhenGivenHolderIsNotRegistered() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry();
        List<Equipment> assignedItems = equipmentRegistry.getAssignedEquipment(holder);

        assertThat(assignedItems).isEmpty();
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

    @Test
    public void getAssignedEquipmentReturnsEmptyOptionalWhenGivenHolderHasNoEquipmentRegistered() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry();
        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(holder, itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    public void getAssignedEquipmentReturnsEmptyOptionalWhenNoRegisteredEquipmentMatchWithGivenItemStack() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(false);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry();
        equipmentRegistry.register(equipment, holder);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(holder, itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    public void getAssignedEquipmentReturnsOptionalContainingEquipmentMatchingWithGivenItemStack() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        DefaultEquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry();
        equipmentRegistry.register(equipment, holder);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(holder, itemStack);

        assertThat(equipmentOptional).hasValue(equipment);
    }
}
