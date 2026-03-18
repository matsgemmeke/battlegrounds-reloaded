package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;

public class HitboxConfigurationProvider implements Provider<HitboxConfiguration> {

    private final File dataFolder;
    private final ObjectValidator objectValidator;
    private final Plugin plugin;

    @Inject
    public HitboxConfigurationProvider(Plugin plugin, ObjectValidator objectValidator, @Named("DataFolder") File dataFolder) {
        this.plugin = plugin;
        this.objectValidator = objectValidator;
        this.dataFolder = dataFolder;
    }

    @Override
    public HitboxConfiguration get() {
        File hitboxFile = new File(dataFolder.getAbsoluteFile(), "/hitboxes.yml");
        InputStream hitboxResource = plugin.getResource("hitboxes.yml");

        HitboxConfiguration configuration = new HitboxConfiguration(objectValidator, hitboxFile, hitboxResource);
        configuration.load();
        return configuration;
    }
}
