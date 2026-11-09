package nl.matsgemmeke.battlegrounds.resource;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;

public class ResourceProvider {

    private final Plugin plugin;

    @Inject
    public ResourceProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    public InputStream getResource(String fileName) {
        InputStream resource = plugin.getResource(fileName);

        if (resource == null) {
            throw new MissingResourceException("Plugin has no resource for file name " + fileName);
        }

        return resource;
    }
}
