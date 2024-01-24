package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.api.entity.RecoilReceiver;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface RecoilSystem {

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
