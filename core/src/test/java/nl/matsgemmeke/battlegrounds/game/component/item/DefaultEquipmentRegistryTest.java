package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
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
        EquipmentUser user = mock(EquipmentUser.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getUser()).thenReturn(user);

        equipmentRegistry.assign(equipment, user);

        assertThat(equipmentRegistry.getAssignedEquipmentList(user)).isEmpty();
    }

    @Test
    @DisplayName("assign adds given equipment to assigned list of given user")
    void assign_equipmentRegistered() {
        EquipmentUser user = mock(EquipmentUser.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getUser()).thenReturn(user);

        equipmentRegistry.register(equipment);
        equipmentRegistry.assign(equipment, user);

        assertThat(equipmentRegistry.getAssignedEquipmentList(user)).containsExactly(equipment);
    }

    @Test
    @DisplayName("unassign does nothing when given equipment has no user")
    void unassign_withoutUser() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getUser()).thenReturn(null);

        assertThatCode(() -> equipmentRegistry.unassign(equipment)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("unassign does nothing when given equipment's user is not registered")
    void unassign_userNotRegistered() {
        EquipmentUser user = mock(EquipmentUser.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getUser()).thenReturn(user);

        equipmentRegistry.unassign(equipment);

        assertThat(equipmentRegistry.getAssignedEquipmentList(user)).isEmpty();
    }

    @Test
    @DisplayName("unassign removes given equipment from user")
    void unassign_registeredUser() {
        EquipmentUser user = mock(EquipmentUser.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getUser()).thenReturn(user);

        equipmentRegistry.register(equipment, user);
        equipmentRegistry.unassign(equipment);

        assertThat(equipmentRegistry.getAssignedEquipmentList(user)).isEmpty();
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
    @DisplayName("getAssignedEquipment returns empty list when given user is not registered")
    void getAssignedEquipmentList_unregisteredUser() {
        EquipmentUser user = mock(EquipmentUser.class);

        List<Equipment> assignedItems = equipmentRegistry.getAssignedEquipmentList(user);

        assertThat(assignedItems).isEmpty();
    }

    @Test
    @DisplayName("getAssignedEquipmentList returns list of all assigned equipment to given user")
    void getAssignedEquipmentList_registeredUser() {
        Equipment equipment = mock(Equipment.class);
        EquipmentUser user = mock(EquipmentUser.class);

        equipmentRegistry.register(equipment, user);
        List<Equipment> assignedItems = equipmentRegistry.getAssignedEquipmentList(user);

        assertThat(assignedItems).containsExactly(equipment);
    }

    @Test
    @DisplayName("getAssignedEquipment returns empty optional when given user is not registered")
    void getAssignedEquipment_unregisteredUser() {
        EquipmentUser user = mock(EquipmentUser.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(user, itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    @DisplayName("getAssignedEquipment returns empty optional when no equipment match with given user and item stack")
    void getAssignedEquipment_noMatchForGivenUserAndItemStack() {
        EquipmentUser user = mock(EquipmentUser.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(false);

        equipmentRegistry.register(equipment, user);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(user, itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    @DisplayName("getAssignedEquipment returns optional containing equipment that matches with given user and item stack")
    void getAssignedEquipment_matchingEquipment() {
        EquipmentUser user = mock(EquipmentUser.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        equipmentRegistry.register(equipment, user);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getAssignedEquipment(user, itemStack);

        assertThat(equipmentOptional).hasValue(equipment);
    }

    @Test
    @DisplayName("getUnassignedEquipment returns empty optional when no unassigned equipment matches given item stack")
    void getUnassignedEquipment_noMatchingEquipment() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(false);

        equipmentRegistry.register(equipment);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getUnassignedEquipment(itemStack);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    @DisplayName("getUnassignedEquipment returns optional with matching unassigned equipment")
    void getUnassignedEquipment_matchingEquipment() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Equipment equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);

        equipmentRegistry.register(equipment);
        Optional<Equipment> equipmentOptional = equipmentRegistry.getUnassignedEquipment(itemStack);

        assertThat(equipmentOptional).hasValue(equipment);
    }
}
