package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class DefaultEquipmentRegistry implements EquipmentRegistry {

    @NotNull
    private final ConcurrentMap<EquipmentHolder, List<Equipment>> assignedEquipment;
    @NotNull
    private final List<Equipment> unassignedEquipment;

    @Inject
    public DefaultEquipmentRegistry() {
        this.assignedEquipment = new ConcurrentHashMap<>();
        this.unassignedEquipment = new ArrayList<>();
    }

    public List<Equipment> getAllEquipment() {
        Stream<Equipment> assignedItemsStream = assignedEquipment.values().stream().flatMap(List::stream);
        Stream<Equipment> unassignedItemsStream = unassignedEquipment.stream();

        return Stream.concat(assignedItemsStream, unassignedItemsStream).toList();
    }

    public List<Equipment> getAssignedEquipment(EquipmentHolder holder) {
        if (assignedEquipment.containsKey(holder)) {
            return Collections.unmodifiableList(assignedEquipment.get(holder));
        }

        return Collections.emptyList();
    }

    public Optional<Equipment> getAssignedEquipment(EquipmentHolder holder, ItemStack itemStack) {
        for (Equipment equipment : assignedEquipment.get(holder)) {
            if (equipment.isMatching(itemStack)) {
                return Optional.of(equipment);
            }
        }

        return Optional.empty();
    }

    public void register(Equipment equipment) {
        unassignedEquipment.add(equipment);
    }

    public void register(Equipment equipment, EquipmentHolder holder) {
        assignedEquipment.computeIfAbsent(holder, h -> new ArrayList<>()).add(equipment);
    }
}
