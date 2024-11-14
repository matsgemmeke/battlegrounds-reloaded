package nl.matsgemmeke.battlegrounds.configuration;

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

public class GeneralDataConfigurationTest {

    private File generalDataFile;
    @TempDir
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        generalDataFile = Files.createFile(tempDir.resolve("general.yml")).toFile();
    }

    @AfterEach
    public void tearDown() {
        System.gc();
    }

    @Test
    public void shouldBeAbleToGetMainLobbyLocation() {
        Location location = new Location(null, 1.0, 2.0, 3.0);

        GeneralDataConfiguration generalData = new GeneralDataConfiguration(generalDataFile);
        generalData.load();
        generalData.setMainLobbyLocation(location);
        generalData.save();
        generalData.load();

        Location result = generalData.getMainLobbyLocation();

        assertEquals(location, result);
    }

    @Test
    public void returnsNullIfMainLobbyIsNotSet() {
        GeneralDataConfiguration generalData = new GeneralDataConfiguration(generalDataFile);
        generalData.load();

        Location result = generalData.getMainLobbyLocation();

        assertNull(result);
    }

    @Test
    public void shouldBeAbleToSetMainLobbyLocation() {
        double x = 1.0;
        double y = 2.0;
        double z = 3.0;

        Location location = new Location(null, x, y, z);

        GeneralDataConfiguration generalData = new GeneralDataConfiguration(generalDataFile);
        generalData.load();
        generalData.setMainLobbyLocation(location);

        Section section = generalData.getSection("main-lobby");

        assertEquals(x, section.getDouble("x"), 0.0);
        assertEquals(y, section.getDouble("y"), 0.0);
        assertEquals(z, section.getDouble("z"), 0.0);
    }
}
