package nl.matsgemmeke.battlegrounds.arena.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

public interface ArenaSettingsConfigurationFactory {

    ArenaSettingsConfiguration create(ConfigurationFile configurationFile);
}
