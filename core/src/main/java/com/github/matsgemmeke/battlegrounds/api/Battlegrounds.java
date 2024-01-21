package com.github.matsgemmeke.battlegrounds.api;

import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * The entry point to the Battlegrounds plugin.
 */
public interface Battlegrounds extends Plugin {

    /**
     * Gets the configuration of the plugin.
     *
     * @return the battlegrounds configuration
     */
    @NotNull
    BattlegroundsConfig getBattlegroundsConfig();

    /**
     * Gets the game provider of the plugin.
     *
     * @return the game provider
     */
    @NotNull
    GameProvider getGameProvider();
}
