package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a process which can be executed by a {@link BattleItemHolder}.
 */
public interface OperatingMode {

    /**
     * Activates the operation.
     *
     * @param holder the entity which executes the operation
     * @return whether the operation was successfully activated
     */
    boolean activate(@NotNull BattleItemHolder holder);

    /**
     * Attempts to cancel the operation.
     *
     * @param holder the entity who cancelled the operation
     */
    void cancel(@NotNull BattleItemHolder holder);
}
