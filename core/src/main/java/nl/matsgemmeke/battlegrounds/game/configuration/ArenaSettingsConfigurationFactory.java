package nl.matsgemmeke.battlegrounds.game.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

public interface ArenaSettingsConfigurationFactory {

    ArenaSettingsConfiguration create(ConfigurationFile configurationFile);
}
