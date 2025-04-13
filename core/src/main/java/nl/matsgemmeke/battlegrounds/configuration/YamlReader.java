package nl.matsgemmeke.battlegrounds.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class YamlReader {

    @NotNull
    private final File file;
    @Nullable
    private final InputStream resource;
    private YamlDocument document;

    public YamlReader(@NotNull File file) {
        this(file, null);
    }

    public YamlReader(@NotNull File file, @Nullable InputStream resource) {
        this.file = file;
        this.resource = resource;
    }

    public boolean contains(@NotNull String route) {
        return document.contains(route);
    }

    @NotNull
    public <T> Optional<T> getAsOptional(@NotNull String route, @NotNull Class<T> clazz) {
        return document.getAsOptional(route, clazz);
    }

    @NotNull
    public Optional<List<Float>> getOptionalFloatList(@NotNull String route) {
        return document.getOptionalFloatList(route);
    }

    @NotNull
    public Optional<Integer> getOptionalInt(@NotNull String route) {
        return document.getOptionalInt(route);
    }

    @NotNull
    public Optional<String> getOptionalString(@NotNull String route) {
        return document.getOptionalString(route);
    }

    @NotNull
    public String getString(@NotNull String route) {
        return document.getString(route);
    }

    public void load() {
        String path = file.getPath().replace("\\", "/");

        if (!file.exists() && resource == null) {
            throw new YamlReadException("Cannot read file '%s': it does not exist".formatted(path));
        }

        if (!path.endsWith(".yml")) {
            throw new YamlReadException("Cannot read file '%s': it does not have a .yml extension".formatted(path));
        }

        try {
            if (resource != null) {
                document = YamlDocument.create(file, resource);
            } else {
                document = YamlDocument.create(file);
            }
        } catch (IOException e) {
            throw new YamlReadException("An error ocurred while loading file '%s': %s".formatted(path, e.getMessage()));
        }
    }
}
