package nl.matsgemmeke.battlegrounds.item.validation.validator;

import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.SoundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidSoundTemplateValidatorTest {

    private static final String SEQUENCE_ID = "equipment-reload-1";

    @Mock
    private SoundService soundService;
    @InjectMocks
    private ValidSoundTemplateValidator validator;

    @ParameterizedTest
    @DisplayName("isValid returns true when given value is null or empty")
    @NullAndEmptySource
    void isValid_nullOrEmptyValue(String value) {
        boolean valid = validator.isValid(value, null);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isValid returns true when given value is an existing sequence id")
    void isValid_existingSequenceId() {
        SoundSequence soundSequence = new SoundSequence(null, null);

        when(soundService.getSoundSequence(SEQUENCE_ID)).thenReturn(Optional.of(soundSequence));

        boolean valid = validator.isValid(SEQUENCE_ID, null);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isValid returns true when given value does not exist as sequence id")
    void isValid_nonexistingSequenceId() {
        when(soundService.getSoundSequence(SEQUENCE_ID)).thenReturn(Optional.empty());

        boolean valid = validator.isValid(SEQUENCE_ID, null);

        assertThat(valid).isFalse();
    }
}
