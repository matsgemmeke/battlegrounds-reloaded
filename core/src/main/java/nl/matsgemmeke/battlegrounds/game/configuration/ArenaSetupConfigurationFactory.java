package nl.matsgemmeke.battlegrounds.game.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

public interface ArenaSetupConfigurationFactory {

    ArenaSetupConfiguration create(ConfigurationFile configurationFile);
}
