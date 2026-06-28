package nl.matsgemmeke.battlegrounds.game.configuration;

import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.TestValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArenaSettingsConfigurationTest {

    private static final int LOBBY_COUNTDOWN_LENGTH = 60;
    private static final int MAX_PLAYERS = 10;
    private static final int MIN_PLAYERS = 2;

    private File settingsFile;
    @TempDir
    private File tempDir;
    private final ObjectValidator objectValidator = TestValidatorFactory.createObjectValidator();

    @BeforeEach
    void setUp() {
        settingsFile = new File(tempDir, "settings.yml");
    }

    @Test
    @DisplayName("getArenaSettings throws InvalidArenaSettingsSpecException when values in configuration are invalid")
    void getArenaSettings_invalid() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/arena-settings-configuration/invalid/settings.yml");
        InputStream resource = new FileInputStream(resourceFile);

        ArenaSettingsConfiguration configuration = new ArenaSettingsConfiguration(objectValidator, settingsFile, resource);
        configuration.load();

        assertThatThrownBy(configuration::getArenaSettings)
                .isInstanceOf(InvalidArenaSettingsSpecException.class)
                .hasMessage("Failed to load arena settings specification");
    }

    @Test
    @DisplayName("getArenaSettings returns ArenaSettingSpec with valid mapped configuration values")
    void getArenaSettings_valid() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/arena-settings-configuration/valid/settings.yml");
        InputStream resource = new FileInputStream(resourceFile);

        ArenaSettingsConfiguration configuration = new ArenaSettingsConfiguration(objectValidator, settingsFile, resource);
        configuration.load();
        ArenaSettingsSpec spec = configuration.getArenaSettings();

        assertThat(spec.lobbyCountdownLength()).isEqualTo(10);
        assertThat(spec.maxPlayers()).isEqualTo(2);
        assertThat(spec.minPlayers()).isEqualTo(1);
    }

    @Test
    @DisplayName("saveArenaSettings throws InvalidArenaSettingsSpecException when given spec is invalid")
    void saveArenaSettings_invalid() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/arena-settings-configuration/valid/settings.yml");
        InputStream resource = new FileInputStream(resourceFile);
        ArenaSettingsSpec spec = new ArenaSettingsSpec(-100, -100, -100);

        ArenaSettingsConfiguration configuration = new ArenaSettingsConfiguration(objectValidator, settingsFile, resource);
        configuration.load();

        assertThatThrownBy(() -> configuration.saveArenaSettings(spec))
                .isInstanceOf(InvalidArenaSettingsSpecException.class)
                .hasMessage("Cannot save invalid arena settings specification");
    }

    @Test
    @DisplayName("saveArenaSettings valid values to configuration file")
    void saveArenaSettings_valid() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/arena-settings-configuration/valid/settings.yml");
        InputStream resource = new FileInputStream(resourceFile);
        ArenaSettingsSpec spec = new ArenaSettingsSpec(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        ArenaSettingsConfiguration configuration = new ArenaSettingsConfiguration(objectValidator, settingsFile, resource);
        configuration.load();
        configuration.saveArenaSettings(spec);

        assertThat(configuration.getInteger("lobby-countdown-length")).isEqualTo(LOBBY_COUNTDOWN_LENGTH);
        assertThat(configuration.getInteger("max-players")).isEqualTo(MAX_PLAYERS);
        assertThat(configuration.getInteger("min-players")).isEqualTo(MIN_PLAYERS);
    }
}
