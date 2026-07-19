package nl.matsgemmeke.battlegrounds.arena.configuration.settings;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.TestValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaSettingsConfigurationTest {

    private static final String LOBBY_COUNTDOWN_LENGTH_PATH = "lobby-countdown-length";
    private static final String MAX_PLAYERS_PATH = "max-players";
    private static final String MIN_PLAYERS_PATH = "min-players";

    private static final int LOBBY_COUNTDOWN_LENGTH = 60;
    private static final int LOBBY_COUNTDOWN_LENGTH_INVALID = -100;
    private static final int MAX_PLAYERS = 10;
    private static final int MAX_PLAYERS_INVALID = -100;
    private static final int MIN_PLAYERS = 2;
    private static final int MIN_PLAYERS_INVALID = -100;

    @Mock
    private ConfigurationFile configurationFile;
    @Spy
    private ObjectValidator objectValidator = TestValidatorFactory.createObjectValidator();
    @InjectMocks
    private ArenaSettingsConfiguration settingsConfiguration;

    @ParameterizedTest
    @CsvSource(value = {"null,10,2,lobby-countdown-length", "30,null,2,max-players", "30,10,null,min-players"}, nullValues = "null")
    @DisplayName("getArenaSettings throws InvalidArenaSettingsSpecException when a required value is missing")
    void getArenaSettings_missingValue(Integer lobbyCountdownLength, Integer maxPlayers, Integer minPlayers, String expectedPath) {
        lenient().when(configurationFile.getInt(LOBBY_COUNTDOWN_LENGTH_PATH)).thenReturn(Optional.ofNullable(lobbyCountdownLength));
        lenient().when(configurationFile.getInt(MAX_PLAYERS_PATH)).thenReturn(Optional.ofNullable(maxPlayers));
        lenient().when(configurationFile.getInt(MIN_PLAYERS_PATH)).thenReturn(Optional.ofNullable(minPlayers));

        assertThatThrownBy(settingsConfiguration::getArenaSettings)
                .isInstanceOf(InvalidArenaSettingsSpecException.class)
                .hasMessage("Missing required value at " + expectedPath);
    }

    @Test
    @DisplayName("getArenaSettings throws InvalidArenaSettingsSpecException when values in configuration are invalid")
    void getArenaSettings_invalid() {
        when(configurationFile.getInt(LOBBY_COUNTDOWN_LENGTH_PATH)).thenReturn(Optional.of(LOBBY_COUNTDOWN_LENGTH_INVALID));
        when(configurationFile.getInt(MAX_PLAYERS_PATH)).thenReturn(Optional.of(MAX_PLAYERS_INVALID));
        when(configurationFile.getInt(MIN_PLAYERS_PATH)).thenReturn(Optional.of(MIN_PLAYERS_INVALID));

        assertThatThrownBy(settingsConfiguration::getArenaSettings)
                .isInstanceOf(InvalidArenaSettingsSpecException.class)
                .hasMessage("Failed to load arena settings specification");
    }

    @Test
    @DisplayName("getArenaSettings returns ArenaSettingSpec with valid mapped configuration values")
    void getArenaSettings_valid() {
        when(configurationFile.getInt(LOBBY_COUNTDOWN_LENGTH_PATH)).thenReturn(Optional.of(LOBBY_COUNTDOWN_LENGTH));
        when(configurationFile.getInt(MAX_PLAYERS_PATH)).thenReturn(Optional.of(MAX_PLAYERS));
        when(configurationFile.getInt(MIN_PLAYERS_PATH)).thenReturn(Optional.of(MIN_PLAYERS));

        ArenaSettingsSpec spec = settingsConfiguration.getArenaSettings();

        assertThat(spec.lobbyCountdownLength()).isEqualTo(LOBBY_COUNTDOWN_LENGTH);
        assertThat(spec.maxPlayers()).isEqualTo(MAX_PLAYERS);
        assertThat(spec.minPlayers()).isEqualTo(MIN_PLAYERS);
    }

    @Test
    @DisplayName("saveArenaSettings throws InvalidArenaSettingsSpecException when given spec is invalid")
    void saveArenaSettings_invalid() {
        ArenaSettingsSpec spec = new ArenaSettingsSpec(LOBBY_COUNTDOWN_LENGTH_INVALID, MAX_PLAYERS_INVALID, MIN_PLAYERS_INVALID);

        assertThatThrownBy(() -> settingsConfiguration.saveArenaSettings(spec))
                .isInstanceOf(InvalidArenaSettingsSpecException.class)
                .hasMessage("Cannot save invalid arena settings specification");
    }

    @Test
    @DisplayName("saveArenaSettings valid values to configuration file")
    void saveArenaSettings_valid() {
        ArenaSettingsSpec spec = new ArenaSettingsSpec(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        settingsConfiguration.saveArenaSettings(spec);

        verify(configurationFile).set(LOBBY_COUNTDOWN_LENGTH_PATH, LOBBY_COUNTDOWN_LENGTH);
        verify(configurationFile).set(MAX_PLAYERS_PATH, MAX_PLAYERS);
        verify(configurationFile).set(MIN_PLAYERS_PATH, MIN_PLAYERS);
    }
}
