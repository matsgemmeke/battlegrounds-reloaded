package nl.matsgemmeke.battlegrounds.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ResourceLoader {

    public void copyResource(@NotNull URI source, @NotNull Path target) throws IOException {
        Path path;

        try {
            path = Paths.get(source);
            this.copyResourcesTo(path, target);
        } catch (FileSystemNotFoundException e) {
            path = this.createJarFilePath(source);
            this.copyResourcesTo(path, target);
        }
    }

    private void copyResourcesTo(@NotNull Path path, @NotNull Path target) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path currentTarget = target.resolve(path.relativize(dir).toString());
                Files.createDirectories(currentTarget);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(path.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private Path createJarFilePath(@NotNull URI source) throws IOException {
        Map<String, String> env = new HashMap<>();
        FileSystem fileSystem = FileSystems.newFileSystem(source, env);
        return fileSystem.provider().getPath(source);
    }
}
