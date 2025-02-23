package nl.matsgemmeke.battlegrounds.configuration.data;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataConfigurationProviderTest {

    @Test
    public void getCreatesNewConfigurationInGivenFolderAndLoadsFile() {
        File dataFolder = new File("src/test/resources");

        DataConfigurationProvider provider = new DataConfigurationProvider(dataFolder);
        DataConfiguration configuration = provider.get();

        assertEquals("success", configuration.getString("test"));
    }
}
