package com.github.matsgemmeke.battlegrounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import org.jetbrains.annotations.NotNull;

public interface ReloadSystem {

    /**
     * Activates a reload operation.
     *
     * @param holder the entity executing the action
     * @return whether the reload operation was successful
     */
    boolean activate(@NotNull BattleItemHolder holder);

    /**
     * Cancels the current reload operation.
     */
    void cancel();
}
