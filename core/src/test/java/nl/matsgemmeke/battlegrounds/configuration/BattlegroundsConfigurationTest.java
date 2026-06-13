package nl.matsgemmeke.battlegrounds.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BattlegroundsConfigurationTest {

    @Mock
    private File configFile;
    @Mock
    private InputStream resource;

    private BattlegroundsConfiguration configuration;

    @BeforeEach
    void setUp(@TempDir File tempDir) throws IOException {
        File resourceFile = new File("src/main/resources/config.yml");

        this.configFile = new File(tempDir, "config.yml");
        this.resource = new FileInputStream(resourceFile);

        configFile.delete();

        configuration = new BattlegroundsConfiguration(configFile, resource);
        configuration.load();
    }

    @Test
    @DisplayName("isReadOnly always returns true")
    void isReadOnly() {
        boolean readOnly = configuration.isReadOnly();

        assertThat(readOnly).isTrue();
    }

    @Test
    @DisplayName("getLanguage returns value from configuration")
    void getLanguage() {
        String language = configuration.getLanguage();

        assertThat(language).isEqualTo("en");
    }

    @Test
    @DisplayName("getCameraMovementRecoilDurationInMilliseconds returns value from configuration")
    void getCameraMovementRecoilDurationInMilliseconds() {
        long cameraMovementRecoilDurationMillis = configuration.getCameraMovementRecoilDurationMillis();

        assertThat(cameraMovementRecoilDurationMillis).isEqualTo(20L);
    }

    @Test
    @DisplayName("getSaveDamageEventsJobPeriodMillis returns value from configuration")
    void getSaveDamageEventsJobPeriodMillis() {
        long saveDamageEventJobPeriodMillis = configuration.getSaveDamageEventsJobPeriodMillis();

        assertThat(saveDamageEventJobPeriodMillis).isEqualTo(60000L);
    }

    @Test
    @DisplayName("isEnabledRegisterPlayersAsPassive returns value from configuration")
    void isEnabledRegisterPlayersAsPassive() {
        boolean enabledRegisterPlayersAsPassive = configuration.isEnabledRegisterPlayersAsPassive();

        assertThat(enabledRegisterPlayersAsPassive).isFalse();
    }
}
