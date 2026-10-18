package nl.matsgemmeke.battlegrounds.sound.validation.validator;

import nl.matsgemmeke.battlegrounds.sound.bukkit.BukkitSoundRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidSoundValidatorTest {

    private static final String SOUND = "ui.button.click";

    @Mock
    private BukkitSoundRegistry bukkitSoundRegistry;
    @InjectMocks
    private ValidSoundValidator validator;

    @ParameterizedTest
    @DisplayName("isValid returns true when given value is null or empty")
    @NullAndEmptySource
    void isValid_nullOrEmpty(String value) {
        boolean valid = validator.isValid(value, null);

        assertThat(valid).isTrue();
    }

    @ParameterizedTest
    @DisplayName("isValid returns true when given value exists in the bukkit sound registry")
    @CsvSource({ "true,true", "false,false" })
    void isValid_existsInSoundRegistry(boolean soundExists, boolean expectedResult) {
        when(bukkitSoundRegistry.exists(SOUND)).thenReturn(soundExists);

        boolean valid = validator.isValid(SOUND, null);

        assertThat(valid).isEqualTo(expectedResult);
    }
}
