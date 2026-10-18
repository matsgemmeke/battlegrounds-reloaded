package nl.matsgemmeke.battlegrounds.sound.configuration;

import com.google.inject.Binder;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.sound.bukkit.BukkitSoundRegistry;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.TestValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundsConfigurationTest {

    private static final String SEQUENCE_ID = "assault-rifle-reload-1";
    private static final String VALID_ENTRY = "ui.button.click 1 2 3";

    @Mock
    private BukkitSoundRegistry bukkitSoundRegistry;
    @Mock
    private ConfigurationFile configurationFile;
    @Mock
    private Logger logger;
    @Spy
    private ObjectValidator objectValidator = TestValidatorFactory.createObjectValidator(this::configure);
    @Mock
    private Section rootSection;
    @InjectMocks
    private SoundsConfiguration soundsConfiguration;

    @BeforeEach
    void setUp() {
        when(configurationFile.getRootSection()).thenReturn(rootSection);
    }

    private void configure(Binder binder) {
        binder.bind(BukkitSoundRegistry.class).toProvider(() -> bukkitSoundRegistry);
    }

    @Test
    @DisplayName("getSoundSequence returns empty optional when given sequence id is not a list")
    void getSoundSequence_noList() {
        when(rootSection.isList(SEQUENCE_ID)).thenReturn(false);

        Optional<SoundSequenceSpec> soundSequenceSpecOptional = soundsConfiguration.getSoundSequence(SEQUENCE_ID);

        assertThat(soundSequenceSpecOptional).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("getSoundSequence returns empty optional when sequence has parsing errors")
    @CsvSource(value = {
            "ui.button.click 1 2      | - entry 1: has 3 parts, expected 4 parts (sound volume pitch delay)",
            "ui.button.click fail 2 0 | - entry 1: volume 'fail' is not a decimal number",
            "ui.button.click 1 fail 0 | - entry 1: pitch 'fail' is not a decimal number",
            "ui.button.click 1 2 fail | - entry 1: delay 'fail' is not a whole number"
    }, delimiter = '|', ignoreLeadingAndTrailingWhitespace = false)
    void getSoundSequence_entryParsingError(String entryValue, String expectedErrorMessage) {
        when(rootSection.isList(SEQUENCE_ID)).thenReturn(true);
        when(rootSection.getStringList(SEQUENCE_ID)).thenReturn(List.of(entryValue));

        Optional<SoundSequenceSpec> soundSequenceSpecOptional = soundsConfiguration.getSoundSequence(SEQUENCE_ID);

        assertThat(soundSequenceSpecOptional).isEmpty();

        verify(logger).warning("Sound sequence 'assault-rifle-reload-1' is invalid and will be ignored");
        verify(logger).warning(expectedErrorMessage);
    }

    @ParameterizedTest
    @DisplayName("getSoundSequence returns empty optional when sequence has invalid id")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    void getSoundSequence_invalidSequenceId(String sequenceId) {
        when(rootSection.isList(sequenceId)).thenReturn(true);
        when(rootSection.getStringList(sequenceId)).thenReturn(List.of(VALID_ENTRY));
        when(bukkitSoundRegistry.exists("ui.button.click")).thenReturn(true);

        Optional<SoundSequenceSpec> soundSequenceSpecOptional = soundsConfiguration.getSoundSequence(sequenceId);

        assertThat(soundSequenceSpecOptional).isEmpty();

        verify(logger).warning("Sound sequence '%s' is invalid and will be ignored".formatted(sequenceId));
        verify(logger).warning(" - sequence id must be a text value");
    }

    @Test
    @DisplayName("getSoundSequence returns empty optional when sequence has no entries")
    void getSoundSequence_withoutEntries() {
        when(rootSection.isList(SEQUENCE_ID)).thenReturn(true);
        when(rootSection.getStringList(SEQUENCE_ID)).thenReturn(List.of());

        Optional<SoundSequenceSpec> soundSequenceSpecOptional = soundsConfiguration.getSoundSequence(SEQUENCE_ID);

        assertThat(soundSequenceSpecOptional).isEmpty();

        verify(logger).warning("Sound sequence 'assault-rifle-reload-1' is invalid and will be ignored");
        verify(logger).warning(" - sequence must have at least one entry");
    }

    @ParameterizedTest
    @DisplayName("getSoundSequence returns empty optional when sequence has entries with validation errors")
    @CsvSource(value = {
            "  1 2 0                 | - sound must not be blank",
            "ui.button.click -1 2 0  | - volume must be 0 or higher (was -1.0)",
            "ui.button.click 1 2 -1  | - delay must be 0 or higher (was -1)"
    }, delimiter = '|', ignoreLeadingAndTrailingWhitespace = false)
    void getSoundSequence_entryValidationError(String entryValue, String expectedErrorMessage) {
        when(rootSection.isList(SEQUENCE_ID)).thenReturn(true);
        when(rootSection.getStringList(SEQUENCE_ID)).thenReturn(List.of(entryValue));
        lenient().when(bukkitSoundRegistry.exists("ui.button.click")).thenReturn(true);

        Optional<SoundSequenceSpec> soundSequenceSpecOptional = soundsConfiguration.getSoundSequence(SEQUENCE_ID);

        assertThat(soundSequenceSpecOptional).isEmpty();

        verify(logger).warning("Sound sequence 'assault-rifle-reload-1' is invalid and will be ignored");
        verify(logger).warning(expectedErrorMessage);
    }

    @Test
    @DisplayName("getSoundSequence returns optional with validated sound sequence specification")
    void getSoundSequence_successful() {
        when(rootSection.isList(SEQUENCE_ID)).thenReturn(true);
        when(rootSection.getStringList(SEQUENCE_ID)).thenReturn(List.of(VALID_ENTRY));
        when(bukkitSoundRegistry.exists("ui.button.click")).thenReturn(true);

        Optional<SoundSequenceSpec> soundSequenceSpecOptional = soundsConfiguration.getSoundSequence(SEQUENCE_ID);

        assertThat(soundSequenceSpecOptional).hasValueSatisfying(spec -> {
            assertThat(spec.id()).isEqualTo(SEQUENCE_ID);
            assertThat(spec.entries()).satisfiesExactly(entry -> {
                assertThat(entry.sound()).isEqualTo("ui.button.click");
                assertThat(entry.volume()).isEqualTo(1);
                assertThat(entry.pitch()).isEqualTo(2);
                assertThat(entry.delay()).isEqualTo(3);
            });
        });
    }
}
