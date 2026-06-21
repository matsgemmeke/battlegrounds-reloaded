package nl.matsgemmeke.battlegrounds.game.configuration;

import java.io.File;
import java.io.InputStream;

public interface ArenaSettingsConfigurationFactory {

    ArenaSettingsConfiguration create(File file, InputStream resource);
}
