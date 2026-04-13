package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;

public class ChangeScopeMagnificationFunction implements Function<GunUser> {

    private final Gun gun;

    public ChangeScopeMagnificationFunction(Gun gun) {
        this.gun = gun;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public FunctionResult perform(GunUser user) {
        if (!gun.isUsingScope()) {
            return FunctionResult.DENIED;
        }

        gun.changeScopeMagnification();

        return FunctionResult.SUCCESS;
    }
}
