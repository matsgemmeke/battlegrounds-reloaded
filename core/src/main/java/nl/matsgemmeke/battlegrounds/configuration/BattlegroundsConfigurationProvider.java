package nl.matsgemmeke.battlegrounds.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;

public class BattlegroundsConfigurationProvider implements Provider<BattlegroundsConfiguration> {

    private final File dataFolder;
    private final Plugin plugin;

    @Inject
    public BattlegroundsConfigurationProvider(@Named("dataFolder") File dataFolder, Plugin plugin) {
        this.dataFolder = dataFolder;
        this.plugin = plugin;
    }

    @Override
    public BattlegroundsConfiguration get() {
        File configFile = new File(dataFolder.getAbsoluteFile(), "/config.yml");
        InputStream configResource = plugin.getResource("config.yml");

        BattlegroundsConfiguration configuration = new BattlegroundsConfiguration(configFile, configResource);
        configuration.load();
        return configuration;
    }
}
