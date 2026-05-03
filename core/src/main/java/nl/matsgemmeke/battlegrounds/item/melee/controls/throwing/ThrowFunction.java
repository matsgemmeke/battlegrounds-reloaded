package nl.matsgemmeke.battlegrounds.item.melee.controls.throwing;

import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;

public class ThrowFunction implements Function<MeleeWeaponUser> {

    private final MeleeWeapon meleeWeapon;

    public ThrowFunction(MeleeWeapon meleeWeapon) {
        this.meleeWeapon = meleeWeapon;
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
    public FunctionResult perform(MeleeWeaponUser user) {
        meleeWeapon.performThrow(user);

        return FunctionResult.SUCCESS;
    }
}
