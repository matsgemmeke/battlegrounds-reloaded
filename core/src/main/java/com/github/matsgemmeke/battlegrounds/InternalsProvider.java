package com.github.matsgemmeke.battlegrounds;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface InternalsProvider {

    void setPlayerRotation(@NotNull Player player, float yaw, float pitch);

    void setScopeLevel(@NotNull Player player, float scopeLevel);
}
