package nl.matsgemmeke.battlegrounds.configuration;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GeneralDataConfiguration extends BasePluginConfiguration {

    public GeneralDataConfiguration(@NotNull File file) {
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
