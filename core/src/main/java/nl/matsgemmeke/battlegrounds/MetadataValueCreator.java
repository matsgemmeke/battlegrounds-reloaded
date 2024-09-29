package nl.matsgemmeke.battlegrounds;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MetadataValueCreator {

    @NotNull
    private Plugin plugin;

    public MetadataValueCreator(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public MetadataValue createFixedMetadataValue(Object value) {
        return new FixedMetadataValue(plugin, value);
    }
}
