package nl.matsgemmeke.battlegrounds.arena.configuration.settings;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

public interface ArenaSettingsConfigurationFactory {

    ArenaSettingsConfiguration create(ConfigurationFile configurationFile);
}
