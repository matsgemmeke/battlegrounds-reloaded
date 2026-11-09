package nl.matsgemmeke.battlegrounds.resource;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceProviderTest {

    private static final String FILE_NAME = "my-resource.txt";

    @Mock
    private Plugin plugin;
    @InjectMocks
    private ResourceProvider resourceProvider;

    @Test
    @DisplayName("getResource throws MissingResourceException when given file name is not a resource")
    void getResource_notFound() {
        when(plugin.getResource(FILE_NAME)).thenReturn(null);

        assertThatThrownBy(() -> resourceProvider.getResource(FILE_NAME))
                .isInstanceOf(MissingResourceException.class)
                .hasMessage("Plugin has no resource for file name my-resource.txt");
    }

    @Test
    @DisplayName("getResource returns corresponding resource as InputStream")
    void getResource_successful() {
        InputStream resource = InputStream.nullInputStream();

        when(plugin.getResource(FILE_NAME)).thenReturn(resource);

        InputStream result = resourceProvider.getResource(FILE_NAME);

        assertThat(result).isEqualTo(resource);
    }
}
