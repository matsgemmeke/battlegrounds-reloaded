package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultGunRegistry implements GunRegistry {

    @NotNull
    private ItemStorage<Gun, GunHolder> gunStorage;

    public DefaultGunRegistry(@NotNull ItemStorage<Gun, GunHolder> gunStorage) {
        this.gunStorage = gunStorage;
    }

    @NotNull
    public List<Gun> findAll() {
        return gunStorage.getAllItems();
    }

    public void registerItem(@NotNull Gun gun) {
        gunStorage.addUnassignedItem(gun);
    }

    public void registerItem(@NotNull Gun gun, @NotNull GunHolder holder) {
        gunStorage.addAssignedItem(gun, holder);
    }

    public void unassignItem(@NotNull Gun gun) {
        GunHolder holder = gun.getHolder();

        if (holder == null) {
            return;
        }

        gunStorage.removeAssignedItem(gun, holder);
        gunStorage.addUnassignedItem(gun);
    }
}
