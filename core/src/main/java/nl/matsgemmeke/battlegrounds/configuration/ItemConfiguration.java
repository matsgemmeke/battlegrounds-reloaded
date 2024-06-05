package nl.matsgemmeke.battlegrounds.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class ItemConfiguration extends BasePluginConfiguration {

    public ItemConfiguration(@NotNull File file, @Nullable InputStream resource) {
        super(file, resource, true);
    }

    @Nullable
    public String getItemId() {
        return this.getString("id");
    }
}
