package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NamespacedKeyCreatorTest {

    private static final String PLUGIN_NAMESPACE = "battlegrounds";
    private static final String KEY_VALUE = "my-test-key";

    @Test
    @DisplayName("create returns new namespaced key with plugin as namespace")
    void create() {
        Plugin plugin = mock(Plugin.class);
        when(plugin.namespace()).thenReturn(PLUGIN_NAMESPACE);

        NamespacedKeyCreator keyCreator = new NamespacedKeyCreator(plugin);
        NamespacedKey key = keyCreator.create(KEY_VALUE);

        assertThat(key.namespace()).isEqualTo(PLUGIN_NAMESPACE);
        assertThat(key.getKey()).isEqualTo(KEY_VALUE);
    }
}
