package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class DefaultEquipmentRegistry implements EquipmentRegistry {

    private final ConcurrentMap<EquipmentHolder, List<Equipment>> assignedEquipment;
    private final List<Equipment> unassignedEquipment;

    public DefaultEquipmentRegistry() {
        this.assignedEquipment = new ConcurrentHashMap<>();
        this.unassignedEquipment = new ArrayList<>();
    }

    @Override
    public void assign(Equipment equipment, EquipmentHolder holder) {
        if (!unassignedEquipment.contains(equipment)) {
            return;
        }

        unassignedEquipment.remove(equipment);
        assignedEquipment.computeIfAbsent(holder, h -> new ArrayList<>()).add(equipment);
    }

    @Override
    public void unassign(Equipment equipment) {
        EquipmentHolder holder = equipment.getHolder();

        if (holder == null || !assignedEquipment.containsKey(holder)) {
            return;
        }

        assignedEquipment.get(holder).remove(equipment);
        unassignedEquipment.add(equipment);
    }

    @Override
    public List<Equipment> getAllEquipment() {
        Stream<Equipment> assignedItemsStream = assignedEquipment.values().stream().flatMap(List::stream);
        Stream<Equipment> unassignedItemsStream = unassignedEquipment.stream();

        return Stream.concat(assignedItemsStream, unassignedItemsStream).toList();
    }

    @Override
    public List<Equipment> getAssignedEquipmentList(EquipmentHolder holder) {
        if (!assignedEquipment.containsKey(holder)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(assignedEquipment.get(holder));
    }

    @Override
    public Optional<Equipment> getAssignedEquipment(EquipmentHolder holder, ItemStack itemStack) {
        if (!assignedEquipment.containsKey(holder)) {
            return Optional.empty();
        }

        for (Equipment equipment : assignedEquipment.get(holder)) {
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
    public void register(Equipment equipment, EquipmentHolder holder) {
        assignedEquipment.computeIfAbsent(holder, h -> new ArrayList<>()).add(equipment);
    }
}
