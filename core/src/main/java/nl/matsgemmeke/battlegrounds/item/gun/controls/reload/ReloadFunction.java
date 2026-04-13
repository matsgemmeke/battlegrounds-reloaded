package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;

public class ReloadFunction implements Function<GunUser> {

    private final Gun gun;

    public ReloadFunction(Gun gun) {
        this.gun = gun;
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public boolean isPerforming() {
        return gun.isReloading();
    }

    @Override
    public boolean cancel() {
        return gun.cancelReload();
    }

    @Override
    public FunctionResult perform(GunUser user) {
        if (!gun.isReloadAvailable()) {
            return FunctionResult.FAILED;
        }

        gun.reload(user);

        return FunctionResult.SUCCESS;
    }
}
