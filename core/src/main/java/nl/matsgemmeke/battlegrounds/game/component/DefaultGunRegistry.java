package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.jetbrains.annotations.NotNull;

public class DefaultGunRegistry implements ItemRegistry<Gun, GunHolder> {

    @NotNull
    private ItemStorage<Gun, GunHolder> gunStorage;

    public DefaultGunRegistry(@NotNull ItemStorage<Gun, GunHolder> gunStorage) {
        this.gunStorage = gunStorage;
    }

    public void registerItem(@NotNull Gun gun) {
        gunStorage.addUnassignedItem(gun);
    }

    public void registerItem(@NotNull Gun gun, @NotNull GunHolder holder) {
        gunStorage.addAssignedItem(gun, holder);
    }
}
