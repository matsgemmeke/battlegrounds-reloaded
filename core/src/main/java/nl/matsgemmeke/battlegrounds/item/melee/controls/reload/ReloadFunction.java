package nl.matsgemmeke.battlegrounds.item.melee.controls.reload;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;

public class ReloadFunction implements ItemFunction<MeleeWeaponUser> {

    private final MeleeWeapon meleeWeapon;

    public ReloadFunction(MeleeWeapon meleeWeapon) {
        this.meleeWeapon = meleeWeapon;
    }

    @Override
    public boolean isAvailable() {
        return meleeWeapon.isReloadAvailable();
    }

    @Override
    public boolean isBlocking() {
        return false;
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
    public boolean perform(MeleeWeaponUser user) {
        if (!this.isAvailable()) {
            return false;
        }

        meleeWeapon.reload(user);
        return true;
    }
}
