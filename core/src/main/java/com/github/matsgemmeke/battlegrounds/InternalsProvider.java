package com.github.matsgemmeke.battlegrounds;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface InternalsProvider {

    void setScopeLevel(@NotNull Player player, float scopeLevel);
}
