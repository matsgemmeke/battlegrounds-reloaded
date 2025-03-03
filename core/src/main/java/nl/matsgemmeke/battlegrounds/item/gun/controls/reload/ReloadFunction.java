package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

public class ReloadFunction implements ItemFunction<GunHolder> {

    @NotNull
    private final Gun gun;

    public ReloadFunction(@NotNull Gun gun) {
        this.gun = gun;
    }

    public boolean isAvailable() {
        return gun.isReloadAvailable();
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return gun.isReloading();
    }

    public boolean cancel() {
        return gun.cancelReload();
    }

    public boolean perform(@NotNull GunHolder holder) {
        if (!this.isAvailable()) {
            return false;
        }

        gun.reload(holder);
        return true;
    }
}
