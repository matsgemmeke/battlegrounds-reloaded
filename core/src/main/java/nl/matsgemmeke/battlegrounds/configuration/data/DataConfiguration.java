package nl.matsgemmeke.battlegrounds.configuration.data;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class DataConfiguration extends BasePluginConfiguration {

    public DataConfiguration(@NotNull File file) {
        super(file, null);
    }

    @Nullable
    public Location getMainLobbyLocation() {
        Section section = this.getSection("main-lobby");

        if (section == null) {
            return null;
        }

        return Location.deserialize(section.getStringRouteMappedValues(false));
    }

    public void setMainLobbyLocation(@NotNull Location location) {
        this.setValue("main-lobby", location.serialize());
    }
}
