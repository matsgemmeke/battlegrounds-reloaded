package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HitboxConfigurationProviderTest {

    @Spy
    private File dataFolder = new File("src/main/resources");
    @Mock
    private Plugin plugin;
    @InjectMocks
    private HitboxConfigurationProvider provider;

    @Test
    void getCreatesNewConfigurationWithConfigFileContent() throws FileNotFoundException {
        File hitboxesFile = new File("src/main/resources/hitboxes.yml");
        FileInputStream inputStream = new FileInputStream(hitboxesFile);

        when(plugin.getResource("hitboxes.yml")).thenReturn(inputStream);

        HitboxConfiguration configuration = provider.get();
        configuration.load();

        assertThat(configuration.getHitboxDefinition("player", "standing")).isNotEmpty();
    }
}
