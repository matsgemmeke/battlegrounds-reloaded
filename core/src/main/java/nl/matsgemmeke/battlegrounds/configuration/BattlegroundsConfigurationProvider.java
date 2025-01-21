package nl.matsgemmeke.battlegrounds.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

public class BattlegroundsConfigurationProvider implements Provider<BattlegroundsConfiguration> {

    @NotNull
    private File dataFolder;
    @NotNull
    private Plugin plugin;

    @Inject
    public BattlegroundsConfigurationProvider(@Named("DataFolder") @NotNull File dataFolder, @NotNull Plugin plugin) {
        this.dataFolder = dataFolder;
        this.plugin = plugin;
    }

    public BattlegroundsConfiguration get() {
        File configFile = new File(dataFolder.getAbsoluteFile(), "/config.yml");
        InputStream configResource = plugin.getResource("config.yml");

        BattlegroundsConfiguration configuration = new BattlegroundsConfiguration(configFile, configResource);
        configuration.load();
        return configuration;
    }
}
