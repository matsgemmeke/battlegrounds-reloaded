package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;

public class HitboxConfigurationProvider implements Provider<HitboxConfiguration> {

    private final File dataFolder;
    private final Plugin plugin;

    @Inject
    public HitboxConfigurationProvider(Plugin plugin, @Named("DataFolder") File dataFolder) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
    }

    @Override
    public HitboxConfiguration get() {
        File hitboxFile = new File(dataFolder.getAbsoluteFile(), "/hitboxes.yml");
        InputStream hitboxResource = plugin.getResource("hitboxes.yml");

        HitboxConfiguration configuration = new HitboxConfiguration(hitboxFile, hitboxResource);
        configuration.load();
        return configuration;
    }
}
