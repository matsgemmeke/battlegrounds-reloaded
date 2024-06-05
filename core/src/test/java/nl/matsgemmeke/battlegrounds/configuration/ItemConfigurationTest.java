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

public class ItemConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File configFile;

    @Before
    public void setUp() throws IOException {
        this.configFile = folder.newFile("guns.yml");

        configFile.delete();
    }

    @Test
    public void canVerifyWeaponId() throws IOException {
        File resourceFile = new File("src/main/resources/items/submachine_guns/mp5.yml");
        InputStream resource = new FileInputStream(resourceFile);

        ItemConfiguration configuration = new ItemConfiguration(configFile, resource);
        configuration.load();

        assertEquals("MP5", configuration.getItemId());
    }
}
