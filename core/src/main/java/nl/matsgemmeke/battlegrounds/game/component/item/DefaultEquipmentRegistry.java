package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class DefaultEquipmentRegistry implements EquipmentRegistry {

    private final ConcurrentMap<EquipmentUser, List<Equipment>> assignedEquipment;
    private final List<Equipment> unassignedEquipment;

    public DefaultEquipmentRegistry() {
        this.assignedEquipment = new ConcurrentHashMap<>();
        this.unassignedEquipment = new ArrayList<>();
    }

    @Override
    public void assign(Equipment equipment, EquipmentUser user) {
        if (!unassignedEquipment.contains(equipment)) {
            return;
        }

        unassignedEquipment.remove(equipment);
        assignedEquipment.computeIfAbsent(user, h -> new ArrayList<>()).add(equipment);
    }

    @Override
    public void unassign(Equipment equipment) {
        EquipmentUser user = equipment.getUser();

        if (user == null || !assignedEquipment.containsKey(user)) {
            return;
        }

        assignedEquipment.get(user).remove(equipment);
        unassignedEquipment.add(equipment);
    }

    @Override
    public List<Equipment> getAllEquipment() {
        Stream<Equipment> assignedItemsStream = assignedEquipment.values().stream().flatMap(List::stream);
        Stream<Equipment> unassignedItemsStream = unassignedEquipment.stream();

        return Stream.concat(assignedItemsStream, unassignedItemsStream).toList();
    }

    @Override
    public List<Equipment> getAssignedEquipmentList(EquipmentUser user) {
        if (!assignedEquipment.containsKey(user)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(assignedEquipment.get(user));
    }

    @Override
    public Optional<Equipment> getAssignedEquipment(EquipmentUser user, ItemStack itemStack) {
        if (!assignedEquipment.containsKey(user)) {
            return Optional.empty();
        }

        for (Equipment equipment : assignedEquipment.get(user)) {
            if (equipment.isMatching(itemStack)) {
                return Optional.of(equipment);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Equipment> getUnassignedEquipment(ItemStack itemStack) {
        for (Equipment equipment : unassignedEquipment) {
            if (equipment.isMatching(itemStack)) {
                return Optional.of(equipment);
            }
        }

        return Optional.empty();
    }

    @Override
    public void register(Equipment equipment) {
        unassignedEquipment.add(equipment);
    }

    @Override
    public void register(Equipment equipment, EquipmentUser user) {
        assignedEquipment.computeIfAbsent(user, h -> new ArrayList<>()).add(equipment);
    }
}
