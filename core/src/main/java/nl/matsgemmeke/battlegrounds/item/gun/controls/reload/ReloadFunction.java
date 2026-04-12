package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;

public class ReloadFunction implements Function<GunUser> {

    private final Gun gun;

    public ReloadFunction(Gun gun) {
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

    public boolean perform(GunUser user) {
        if (!this.isAvailable()) {
            return false;
        }

        gun.reload(user);
        return true;
    }
}
