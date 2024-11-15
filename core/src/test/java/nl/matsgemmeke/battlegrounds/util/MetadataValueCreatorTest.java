package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class MetadataValueCreatorTest {

    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        plugin = mock(Plugin.class);
    }

    @Test
    public void shouldCreateFixedMetadataValue() {
        int value = 100;

        MetadataValueCreator metadataValueCreator = new MetadataValueCreator(plugin);
        MetadataValue metadataValue = metadataValueCreator.createFixedMetadataValue(value);

        assertInstanceOf(FixedMetadataValue.class, metadataValue);
        assertEquals(value, metadataValue.asInt());
    }
}
