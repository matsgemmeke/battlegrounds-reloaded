package nl.matsgemmeke.battlegrounds.util;

import com.google.inject.Inject;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class NamespacedKeyCreator {

    private final Plugin plugin;

    @Inject
    public NamespacedKeyCreator(Plugin plugin) {
        this.plugin = plugin;
    }

    public NamespacedKey create(String key) {
        return new NamespacedKey(plugin, key);
    }
}
