package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;

public interface DestructionListener {

    /**
     * Gets called when the corresponding deployment object is destroyed.
     *
     * @param damage the final damage that caused the destruction
     */
    void onDestroyed(Damage damage);
}
