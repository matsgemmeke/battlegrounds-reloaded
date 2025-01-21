package nl.matsgemmeke.battlegrounds.configuration.data;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DataConfigurationTest {

    private File dataFile;
    @TempDir
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        dataFile = Files.createFile(tempDir.resolve("data.yml")).toFile();
    }

    @AfterEach
    public void tearDown() {
        // Activate garbage collector to release file lock
        System.gc();
    }

    @Test
    public void shouldBeAbleToGetMainLobbyLocation() {
        Location location = new Location(null, 1.0, 2.0, 3.0);

        DataConfiguration dataConfiguration = new DataConfiguration(dataFile);
        dataConfiguration.load();
        dataConfiguration.setMainLobbyLocation(location);
        dataConfiguration.save();
        dataConfiguration.load();

        Location result = dataConfiguration.getMainLobbyLocation();

        assertEquals(location, result);
    }

    @Test
    public void returnsNullIfMainLobbyIsNotSet() {
        DataConfiguration dataConfiguration = new DataConfiguration(dataFile);
        dataConfiguration.load();

        Location result = dataConfiguration.getMainLobbyLocation();

        assertNull(result);
    }

    @Test
    public void shouldBeAbleToSetMainLobbyLocation() {
        double x = 1.0;
        double y = 2.0;
        double z = 3.0;

        Location location = new Location(null, x, y, z);

        DataConfiguration dataConfiguration = new DataConfiguration(dataFile);
        dataConfiguration.load();
        dataConfiguration.setMainLobbyLocation(location);

        Section section = dataConfiguration.getSection("main-lobby");

        assertEquals(x, section.getDouble("x"), 0.0);
        assertEquals(y, section.getDouble("y"), 0.0);
        assertEquals(z, section.getDouble("z"), 0.0);
    }
}
