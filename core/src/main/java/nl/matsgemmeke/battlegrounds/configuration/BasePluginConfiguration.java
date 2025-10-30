package nl.matsgemmeke.battlegrounds.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public abstract class BasePluginConfiguration implements PluginConfiguration {

    private final boolean readOnly;
    @NotNull
    private final File file;
    @Nullable
    private final InputStream resource;
    private YamlDocument document;

    public BasePluginConfiguration(@NotNull File file, @Nullable InputStream resource) {
        this(file, resource, false);
    }

    public BasePluginConfiguration(@NotNull File file, @Nullable InputStream resource, boolean readOnly) {
        this.file = file;
        this.resource = resource;
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @NotNull
    public Section createSection(@NotNull String path) {
        return document.createSection(path);
    }

    @Nullable
    public Object get(@NotNull String path) {
        return document.get(path);
    }

    public boolean getBoolean(@NotNull String path) {
        return document.getBoolean(path);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        return document.getBoolean(path, def);
    }

    public double getDouble(@NotNull String path) {
        return document.getDouble(path);
    }

    public double getDouble(@NotNull String path, double def) {
        return document.getDouble(path, def);
    }

    public int getInteger(@NotNull String path) {
        return document.getInt(path);
    }

    public int getInteger(@NotNull String path, int def) {
        return document.getInt(path, def);
    }

    public Optional<Double> getOptionalDouble(String route) {
        return document.getOptionalDouble(route);
    }

    public Optional<Section> getOptionalSection(String route) {
        return document.getOptionalSection(route);
    }

    @NotNull
    public Section getRoot() {
        return document.getRoot();
    }

    @Nullable
    public Section getSection(@NotNull String path) {
        return document.getSection(path);
    }

    @Nullable
    public String getString(@NotNull String path) {
        return document.getString(path);
    }

    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        return document.getString(path, def);
    }

    public boolean load() {
        try {
            if (resource != null) {
                document = YamlDocument.create(file, resource);
            } else {
                document = YamlDocument.create(file);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try {
            document.save();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void setValue(@NotNull String path, @NotNull Object value) {
        if (readOnly) {
            throw new UnsupportedOperationException("Configuration file is read-only");
        }

        document.set(path, value);
    }
}
