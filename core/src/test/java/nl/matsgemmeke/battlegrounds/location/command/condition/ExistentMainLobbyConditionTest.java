package nl.matsgemmeke.battlegrounds.location.command.condition;

import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistentMainLobbyConditionTest {

    private static final TextTemplate MAIN_LOBBY_NOT_EXISTS_TEXT_TEMPLATE = LanguageFixture.getTextTemplate(TranslationKey.MAIN_LOBBY_NOT_EXISTS.getPath());

    @Mock
    private LocationConfiguration locationConfiguration;
    @Mock
    private Translator translator;
    @InjectMocks
    private ExistentMainLobbyCondition condition;

    @Test
    @DisplayName("validateCondition does nothing when main lobby is present")
    void validateCondition_mainLobbyPresent() {
        LocationData locationData = new LocationData(null, null, null, null, null, null);

        when(locationConfiguration.getMainLobbyLocation()).thenReturn(Optional.of(locationData));

        assertThatNoException().isThrownBy(() -> condition.validateCondition(null));
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when main lobby is not present")
    void validateCondition_mainLobbyNotPresent() {
        when(locationConfiguration.getMainLobbyLocation()).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.MAIN_LOBBY_NOT_EXISTS.getPath())).thenReturn(MAIN_LOBBY_NOT_EXISTS_TEXT_TEMPLATE);

        assertThatThrownBy(() -> condition.validateCondition(null))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("&cThere is currently no waiting lobby set up.");
    }
}
