package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultGunRegistry implements GunRegistry {

    @NotNull
    private final ItemContainer<Gun, GunHolder> gunContainer;

    public DefaultGunRegistry(@NotNull ItemContainer<Gun, GunHolder> gunContainer) {
        this.gunContainer = gunContainer;
    }

    @NotNull
    public List<Gun> findAll() {
        return gunContainer.getAllItems();
    }

    @NotNull
    public List<Gun> getAssignedItems(@NotNull GunHolder holder) {
        return gunContainer.getAssignedItems(holder);
    }

    public void registerItem(@NotNull Gun gun) {
        gunContainer.addUnassignedItem(gun);
    }

    public void registerItem(@NotNull Gun gun, @NotNull GunHolder holder) {
        gunContainer.addAssignedItem(gun, holder);
    }

    public void unassignItem(@NotNull Gun gun) {
        GunHolder holder = gun.getHolder();

        if (holder == null) {
            return;
        }

        gunContainer.removeAssignedItem(gun, holder);
        gunContainer.addUnassignedItem(gun);
    }
}
