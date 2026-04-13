package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;

public class ShootFunction implements Function<GunUser> {

    private final Gun gun;

    public ShootFunction(Gun gun) {
        this.gun = gun;
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public boolean isPerforming() {
        return gun.isShooting();
    }

    @Override
    public boolean cancel() {
        gun.cancelShooting();
        return true;
    }

    @Override
    public FunctionResult perform(GunUser user) {
        if (!gun.canShoot()) {
            return FunctionResult.DENIED;
        }

        gun.shoot(user);

        return FunctionResult.SUCCESS;
    }
}
