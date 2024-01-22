package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a process which can be executed by a {@link ItemHolder}.
 */
public interface OperatingMode {

    /**
     * Activates the operation.
     *
     * @param holder the entity which executes the operation
     * @return whether the operation was successfully activated
     */
    boolean activate(@NotNull ItemHolder holder);

    /**
     * Attempts to cancel the operation.
     *
     * @param holder the entity who cancelled the operation
     */
    void cancel(@NotNull ItemHolder holder);
}
