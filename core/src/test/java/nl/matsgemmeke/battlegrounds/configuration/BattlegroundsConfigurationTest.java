package nl.matsgemmeke.battlegrounds.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BattlegroundsConfigurationTest {

    private File configFile;
    private InputStream resource;

    @BeforeEach
    public void setUp(@TempDir File tempDir) throws IOException {
        File resourceFile = new File("src/main/resources/config.yml");

        this.configFile = new File(tempDir, "config.yml");
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
    public void canReadGunDamageAmplifier() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertEquals(1.0, config.getGunDamageAmplifier(), 0.0);
    }

    @Test
    public void canReadGunTriggerSound() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertEquals("UI_BUTTON_CLICK-0.5-2-0", config.getGunTriggerSound());
    }

    @Test
    public void canReadRegisterPlayersAsPassive() {
        BattlegroundsConfiguration config = new BattlegroundsConfiguration(configFile, resource);
        config.load();

        assertFalse(config.isEnabledRegisterPlayersAsPassive());
    }
}
