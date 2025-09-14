package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultGunRegistry implements GunRegistry {

    @NotNull
    private final ConcurrentMap<GunHolder, List<Gun>> assignedGuns;
    @NotNull
    private final List<Gun> unassignedGuns;

    public DefaultGunRegistry() {
        this.assignedGuns = new ConcurrentHashMap<>();
        this.unassignedGuns = new ArrayList<>();
    }

    public void assign(Gun gun, GunHolder holder) {
        if (!unassignedGuns.contains(gun)) {
            return;
        }

        unassignedGuns.remove(gun);
        assignedGuns.computeIfAbsent(holder, h -> new ArrayList<>()).add(gun);
    }

    public void unassign(Gun gun) {
        GunHolder holder = gun.getHolder();

        if (holder == null || !assignedGuns.containsKey(holder)) {
            return;
        }

        assignedGuns.get(holder).remove(gun);
        unassignedGuns.add(gun);
    }

    public Optional<Gun> getAssignedGun(GunHolder holder, ItemStack itemStack) {
        if (!assignedGuns.containsKey(holder)) {
            return Optional.empty();
        }

        for (Gun gun : assignedGuns.get(holder)) {
            if (gun.isMatching(itemStack)) {
                return Optional.of(gun);
            }
        }

        return Optional.empty();
    }

    public List<Gun> getAssignedGuns(GunHolder holder) {
        return assignedGuns.getOrDefault(holder, Collections.emptyList());
    }

    public Optional<Gun> getUnassignedGun(ItemStack itemStack) {
        for (Gun gun : unassignedGuns) {
            if (gun.isMatching(itemStack)) {
                return Optional.of(gun);
            }
        }

        return Optional.empty();
    }

    public void register(Gun gun) {
        unassignedGuns.add(gun);
    }

    public void register(Gun gun, GunHolder holder) {
        assignedGuns.computeIfAbsent(holder, h -> new ArrayList<>()).add(gun);
    }
}
