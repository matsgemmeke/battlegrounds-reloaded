package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

public class ShootFunction implements ItemFunction<GunHolder> {

    @NotNull
    private final Gun gun;

    public ShootFunction(@NotNull Gun gun) {
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

    public boolean perform(@NotNull GunHolder holder) {
        if (!this.isAvailable()) {
            return false;
        }

        gun.shoot(holder);
        return true;
    }
}
