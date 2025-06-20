package nl.matsgemmeke.battlegrounds.item.recoil;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the recoil behavior of shootable item in the plugin.
 * <p>
 * Implementations of this interface define how recoil is applied to a {@link RecoilReceiver} when a shot is performed.
 * Recoil can affect the entity's aim, velocity, camera shake, or any other gameplay-affecting elements.
 */
public interface Recoil {

    /**
     * Produces a recoil effect for a single shot by the item.
     *
     * @param receiver the entity who receives the recoil
     * @param direction the direction of the shot
     * @return the new direction of the shot after the recoil effect
     */
    @NotNull
    Location produceRecoil(@NotNull RecoilReceiver receiver, @NotNull Location direction);
}
