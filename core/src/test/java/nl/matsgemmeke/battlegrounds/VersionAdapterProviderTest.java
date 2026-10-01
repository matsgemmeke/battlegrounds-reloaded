package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.compatibility.VersionAdapterInstantiator;
import nl.matsgemmeke.battlegrounds.compatibility.VersionAdapterProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionAdapterProviderTest {

    private static final String SUPPORTED_VERSION = "26.3-R0.1-SNAPSHOT";
    private static final String UNSUPPORTED_VERSION = "1.21.1-R0.1-SNAPSHOT";

    @Mock
    private Supplier<String> bukkitVersionSupplier;
    @Mock
    private VersionAdapterInstantiator versionAdapterInstantiator;
    @InjectMocks
    private VersionAdapterProvider provider;

    @Test
    @DisplayName("get returns VersionAdapter instance for supported version")
    void get_successful() throws ReflectiveOperationException {
        VersionAdapter versionAdapter = mock(VersionAdapter.class);

        when(bukkitVersionSupplier.get()).thenReturn(SUPPORTED_VERSION);
        when(versionAdapterInstantiator.instantiate("nl.matsgemmeke.battlegrounds.nms.v26.V26VersionAdapter")).thenReturn(versionAdapter);

        VersionAdapter result = provider.get();

        assertThat(result).isEqualTo(versionAdapter);
    }

    @Test
    @DisplayName("get throws StartupFailedException when bukkit version is not supported")
    void get_unsupported() {
        when(bukkitVersionSupplier.get()).thenReturn(UNSUPPORTED_VERSION);

        assertThatThrownBy(provider::get)
                .isInstanceOf(StartupFailedException.class)
                .hasMessage("Unsupported Minecraft version: 1.21.1-R0.1-SNAPSHOT");
    }
}
