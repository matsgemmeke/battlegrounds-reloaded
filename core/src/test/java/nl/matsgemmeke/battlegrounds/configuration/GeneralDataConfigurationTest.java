package nl.matsgemmeke.battlegrounds.configuration;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Location;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GeneralDataConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File generalDataFile;

    @Before
    public void setUp() throws IOException {
        this.generalDataFile = folder.newFile("general.yml");
    }

    @Test
    public void shouldBeAbleToGetMainLobbyLocation() {
        double x = 1.0;
        double y = 2.0;
        double z = 3.0;

        Location location = new Location(null, x, y, z);

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
