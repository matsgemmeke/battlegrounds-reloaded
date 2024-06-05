package nl.matsgemmeke.battlegrounds.configuration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class BattlegroundsConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File configFile;
    private InputStream resource;

    @Before
    public void setUp() throws IOException {
        File bgFolder = folder.newFolder("Battlegrounds");
        File resourceFile = new File("src/main/resources/config.yml");

        this.configFile = new File(bgFolder, "config.yml");
        this.resource = new FileInputStream(resourceFile);

        configFile.delete();
    }

    @Test
    public void shouldCreateNewFileWithResourceContentsUponFirstLoad() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertNotNull(config.getString("version"));
    }

    @Test
    public void shouldAlwaysBeReadOnly() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);

        assertTrue(config.isReadOnly());
    }

    @Test
    public void shouldBeAbleToGetLanguage() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertEquals("en", config.getLanguage());
    }

    @Test
    public void canReadCameraMovementRecoilDurationInMillis() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertEquals(20, config.getCameraMovementRecoilDurationInMilliseconds());
    }

    @Test
    public void canReadFirearmDamageAmplifier() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertEquals(1.0, config.getGunDamageAmplifier(), 0.0);
    }

    @Test
    public void canReadFirearmTriggerSound() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertEquals("UI_BUTTON_CLICK-0.5-2-0", config.getGunTriggerSound());
    }
}
