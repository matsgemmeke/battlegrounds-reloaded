package nl.matsgemmeke.battlegrounds.item.recoil;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * An item capable of producing recoil.
 */
public interface RecoilProducer {

    /**
     * Produces a recoil effect for a single shot by a weapon.
     *
     * @param receiver the entity who receives the recoil
     * @param direction the direction of the shot
     * @return the new direction of the shot after the recoil effect
     */
    @NotNull
    Location produceRecoil(@NotNull RecoilReceiver receiver, @NotNull Location direction);
}
