package nl.matsgemmeke.battlegrounds.arena.configuration.setup;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

public interface ArenaSetupConfigurationFactory {

    ArenaSetupConfiguration create(ConfigurationFile configurationFile);
}
