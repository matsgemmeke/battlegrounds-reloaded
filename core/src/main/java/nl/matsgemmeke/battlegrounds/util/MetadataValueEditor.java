package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MetadataValueEditor {

    @NotNull
    private Plugin plugin;

    public MetadataValueEditor(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public void addFixedMetadataValue(@NotNull Metadatable object, @NotNull String key, @NotNull Object value) {
        object.setMetadata(key, new FixedMetadataValue(plugin, value));
    }

    public void removeMetadata(@NotNull Metadatable object, @NotNull String key) {
        object.removeMetadata(key, plugin);
    }
}
