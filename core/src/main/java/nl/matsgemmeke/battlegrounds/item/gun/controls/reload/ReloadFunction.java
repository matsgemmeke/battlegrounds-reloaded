package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import org.jetbrains.annotations.NotNull;

public class ReloadFunction implements ItemFunction<GunHolder> {

    @NotNull
    private final Gun gun;
    @NotNull
    private final ReloadSystem reloadSystem;

    public ReloadFunction(@NotNull Gun gun, @NotNull ReloadSystem reloadSystem) {
        this.gun = gun;
        this.reloadSystem = reloadSystem;
    }

    public boolean isAvailable() {
        return !reloadSystem.isPerforming() && gun.getMagazineAmmo() < gun.getMagazineSize() && gun.getReserveAmmo() > 0;
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return reloadSystem.isPerforming();
    }

    public boolean cancel() {
        return reloadSystem.cancelReload();
    }

    public boolean perform(@NotNull GunHolder holder) {
        if (!this.isAvailable()) {
            return false;
        }

        return reloadSystem.performReload(holder);
    }
}
