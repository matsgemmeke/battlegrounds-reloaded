package nl.matsgemmeke.battlegrounds.arena.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

public interface ArenaSetupConfigurationFactory {

    ArenaSetupConfiguration create(ConfigurationFile configurationFile);
}
