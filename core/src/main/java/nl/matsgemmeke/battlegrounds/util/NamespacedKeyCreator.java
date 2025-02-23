package nl.matsgemmeke.battlegrounds.util;

import com.google.inject.Inject;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class NamespacedKeyCreator {

    @NotNull
    private Plugin plugin;

    @Inject
    public NamespacedKeyCreator(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public NamespacedKey create(@NotNull String key) {
        return new NamespacedKey(plugin, key);
    }
}
