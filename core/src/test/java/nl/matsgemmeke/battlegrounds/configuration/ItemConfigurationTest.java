package nl.matsgemmeke.battlegrounds.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ItemConfigurationTest {

    private File configFile;

    @BeforeEach
    public void setUp(@TempDir File tempDir) throws IOException {
        this.configFile = new File(tempDir, "guns.yml");

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
