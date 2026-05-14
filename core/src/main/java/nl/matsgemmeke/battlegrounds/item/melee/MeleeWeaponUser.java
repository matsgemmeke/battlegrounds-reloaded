package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.ItemUser;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowPerformer;

public interface MeleeWeaponUser extends ItemUser, ReloadPerformer, ThrowPerformer {

    /**
     * Returns a float value between 0.0 and 1.0, represents the amount of relative damage the user does when
     * performing an attack.
     *
     * @return the relative attack strength
     */
    float getAttackStrength();
}
