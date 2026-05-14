package nl.matsgemmeke.battlegrounds.item.melee.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;

public class ReloadFunction implements Function<MeleeWeaponUser> {

    private final MeleeWeapon meleeWeapon;

    public ReloadFunction(MeleeWeapon meleeWeapon) {
        this.meleeWeapon = meleeWeapon;
    }

    @Override
    public boolean isPerforming() {
        return meleeWeapon.isReloading();
    }

    @Override
    public boolean cancel() {
        return meleeWeapon.cancelReload();
    }

    @Override
    public FunctionResult perform(MeleeWeaponUser user) {
        if (!meleeWeapon.isReloadAvailable()) {
            return FunctionResult.FAILED;
        }

        meleeWeapon.reload(user);

        return FunctionResult.SUCCESS;
    }
}
