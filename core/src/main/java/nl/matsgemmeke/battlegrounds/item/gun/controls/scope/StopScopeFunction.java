package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;

public class StopScopeFunction implements ItemFunction<GunUser> {

    private final Gun gun;

    public StopScopeFunction(Gun gun) {
        this.gun = gun;
    }

    public boolean isAvailable() {
        return gun.isUsingScope();
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(GunUser user) {
        if (!this.isAvailable()) {
            return false;
        }

        gun.cancelScope();
        return true;
    }
}
