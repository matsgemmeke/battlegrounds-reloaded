package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NamespacedKeyCreatorTest {

    @Test
    public void createNewNamespacedKeyWithPluginAsNamespace() {
        String keyValue = "battlegrounds-test-key";
        String pluginName = "Battlegrounds";

        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn(pluginName);

        NamespacedKeyCreator keyCreator = new NamespacedKeyCreator(plugin);
        NamespacedKey key = keyCreator.create(keyValue);

        assertEquals(keyValue, key.getKey());
        assertEquals(pluginName.toLowerCase(), key.getNamespace());
    }
}
