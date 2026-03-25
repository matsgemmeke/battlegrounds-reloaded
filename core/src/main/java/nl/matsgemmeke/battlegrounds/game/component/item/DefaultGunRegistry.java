package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultGunRegistry implements GunRegistry {

    private final ConcurrentMap<GunUser, List<Gun>> assignedGuns;
    private final List<Gun> unassignedGuns;

    public DefaultGunRegistry() {
        this.assignedGuns = new ConcurrentHashMap<>();
        this.unassignedGuns = new ArrayList<>();
    }

    @Override
    public void assign(Gun gun, GunUser user) {
        if (!unassignedGuns.contains(gun)) {
            return;
        }

        unassignedGuns.remove(gun);
        assignedGuns.computeIfAbsent(user, h -> new ArrayList<>()).add(gun);
    }

    @Override
    public void unassign(Gun gun) {
        GunUser user = gun.getUser();

        if (user == null || !assignedGuns.containsKey(user)) {
            return;
        }

        assignedGuns.get(user).remove(gun);
        unassignedGuns.add(gun);
    }

    @Override
    public Optional<Gun> getAssignedGun(GunUser user, ItemStack itemStack) {
        if (!assignedGuns.containsKey(user)) {
            return Optional.empty();
        }

        for (Gun gun : assignedGuns.get(user)) {
            if (gun.isMatching(itemStack)) {
                return Optional.of(gun);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Gun> getAssignedGuns(GunUser user) {
        return assignedGuns.getOrDefault(user, Collections.emptyList());
    }

    @Override
    public Optional<Gun> getUnassignedGun(ItemStack itemStack) {
        for (Gun gun : unassignedGuns) {
            if (gun.isMatching(itemStack)) {
                return Optional.of(gun);
            }
        }

        return Optional.empty();
    }

    @Override
    public void register(Gun gun) {
        unassignedGuns.add(gun);
    }

    @Override
    public void register(Gun gun, GunUser user) {
        assignedGuns.computeIfAbsent(user, h -> new ArrayList<>()).add(gun);
    }
}
