package nl.matsgemmeke.battlegrounds;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class MetadataValueCreatorTest {

    private Plugin plugin;

    @Before
    public void setUp() {
        plugin = mock(Plugin.class);
    }

    @Test
    public void shouldCreateFixedMetadataValue() {
        int value = 100;

        MetadataValueCreator metadataValueCreator = new MetadataValueCreator(plugin);
        MetadataValue metadataValue = metadataValueCreator.createFixedMetadataValue(value);

        assertTrue(metadataValue instanceof FixedMetadataValue);
        assertEquals(value, metadataValue.asInt());
    }
}
