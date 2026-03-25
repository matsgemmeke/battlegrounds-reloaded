package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;

public class ShootFunction implements ItemFunction<GunUser> {

    private final Gun gun;

    public ShootFunction(Gun gun) {
        this.gun = gun;
    }

    public boolean isAvailable() {
        return gun.canShoot();
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return gun.isShooting();
    }

    public boolean cancel() {
        gun.cancelShooting();
        return true;
    }

    public boolean perform(GunUser user) {
        if (!this.isAvailable()) {
            return false;
        }

        gun.shoot(user);
        return true;
    }
}
