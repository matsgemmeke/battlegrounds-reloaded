package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

class DefaultEquipmentRegistryTest {

    private DefaultEquipmentRegistry equipmentRegistry;

    @BeforeEach
    void setUp() {
        equipmentRegistry = new DefaultEquipmentRegistry();
    }

    @Test
    @DisplayName("assign does nothing when given equipment is not registered")
    void assign_equipmentNotRegistered() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(holder);

        equipmentRegistry.assign(equipment, holder);

        assertThat(equipmentRegistry.getAssignedEquipmentList(holder)).isEmpty();
    }

    @Test
    @DisplayName("assign adds given equipment to assigned list of given holder")
    void assign_equipmentRegistered() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(holder);

        equipmentRegistry.register(equipment);
        equipmentRegistry.assign(equipment, holder);

        assertThat(equipmentRegistry.getAssignedEquipmentList(holder)).containsExactly(equipment);
    }

    @Test
    @DisplayName("unassign does nothing when given equipment has no holder")
    void unassign_withoutHolder() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(null);

        assertThatCode(() -> equipmentRegistry.unassign(equipment)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("unassign does nothing when given equipment's holder is not registered")
    void unassign_holderNotRegistered() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(holder);

        equipmentRegistry.unassign(equipment);

        assertThat(equipmentRegistry.getAssignedEquipmentList(holder)).isEmpty();
    }

    @Test
    @DisplayName("unassign removes given equipment from holder")
    void unassign_registeredHolder() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(holder);

        equipmentRegistry.register(equipment, holder);
        equipmentRegistry.unassign(equipment);

        assertThat(equipmentRegistry.getAssignedEquipmentList(holder)).isEmpty();
    }

    @Test
    @DisplayName("getAllEquipment returns list containing all equipment items")
    void getAllEquipment_returnsAll() {
        Equipment equipment = mock(Equipment.class);

        equipmentRegistry.register(equipment);
        List<Equipment> equipmentList = equipmentRegistry.getAllEquipment();

        assertThat(equipmentList).containsExactly(equipment);
    }

    @Test
    @DisplayName("getAssignedEquipment returns empty list when given holder is not registered")
    void getAssignedEquipmentList_unregisteredHolder() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        List<Equipment> assignedItems = equipmentRegistry.getAssignedEquipmentList(holder);

        assertThat(assignedItems).isEmpty();
    }

    @Test
    @DisplayName("getAssignedEquipmentList returns list of all assigned equipment to given holder")
    void getAssignedEquipmentList_registeredHolder() {
        Equipment equipment = mock(Equipment.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        equipmentRegistry.register(equipment, holder);
        List<Equipment> assignedItems = equipmentRegistry.getAssignedEquipmentList(holder);

        assertThat(assignedItems).containsExactly(equipment);
    }

    @Test
    @DisplayName("getAssignedEquipment returns empty optional when given holder is not registered")
    void getAssignedEquipment_unregisteredHolder() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(holder, itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    @DisplayName("getAssignedEquipment returns empty optional when no equipment match with given holder and item stack")
    void getAssignedEquipment_noMatchForGivenHolderAndItemStack() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(false);

        equipmentRegistry.register(equipment, holder);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(holder, itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    @DisplayName("getAssignedEquipment returns optional containing equipment that matches with given holder and item stack")
    void getAssignedEquipment_matchingEquipment() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        equipmentRegistry.register(equipment, holder);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(holder, itemStack);

        assertThat(equipmentOptional).hasValue(equipment);
    }

    @Test
    @DisplayName("getUnassignedEquipment returns empty optional when no unassigned equipment matches given item stack")
    void getUnassignedEquipment_noMatch() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(false);

        equipmentRegistry.register(equipment);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getUnassignedEquipment(itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    @DisplayName("getUnassignedEquipment returns optional with matching unassigned equipment")
    void getUnassignedGunReturnsOptionalWithMatchingUnassignedGun() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        equipmentRegistry.register(equipment);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getUnassignedEquipment(itemStack);

        assertThat(equipmentOptional).hasValue(equipment);
    }
}
