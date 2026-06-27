package nl.matsgemmeke.battlegrounds.game.configuration;

import java.io.File;

public class ArenaSetupConfigurationFactory {

    public ArenaSetupConfiguration create(File file) {
        return new ArenaSetupConfiguration(file);
    }
}
