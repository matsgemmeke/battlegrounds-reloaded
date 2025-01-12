package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MetadataValueEditorTest {

    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        plugin = mock(Plugin.class);
    }

    @Test
    public void addFixedMetadataValueAddsFixedMetadataToGivenObject() {
        Metadatable object = mock(Metadatable.class);
        String key = "test";
        int value = 100;

        MetadataValueEditor metadataValueEditor = new MetadataValueEditor(plugin);
        metadataValueEditor.addFixedMetadataValue(object, key, value);

        ArgumentCaptor<FixedMetadataValue> metadataValueCaptor = ArgumentCaptor.forClass(FixedMetadataValue.class);
        verify(object).setMetadata(eq(key), metadataValueCaptor.capture());

        assertEquals(value, metadataValueCaptor.getValue().value());
    }

    @Test
    public void removeMetadataRemovesMetadataFromGivenObject() {
        Metadatable object = mock(Metadatable.class);
        String key = "test";

        MetadataValueEditor metadataValueEditor = new MetadataValueEditor(plugin);
        metadataValueEditor.removeMetadata(object, key);

        verify(object).removeMetadata(key, plugin);
    }
}
