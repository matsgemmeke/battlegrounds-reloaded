package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface RecoilSystem {

    /**
     * Produces a recoil effect for a single shot by a gun.
     *
     * @param holder the entity who operates the gun
     * @param direction the direction of the shot
     * @param relativeAccuracy the relative accuracy of the shot
     * @return the new direction of the shot after the recoil effect
     */
    @NotNull
    Location produceRecoil(@NotNull BattleItemHolder holder, @NotNull Location direction, double relativeAccuracy);
}
