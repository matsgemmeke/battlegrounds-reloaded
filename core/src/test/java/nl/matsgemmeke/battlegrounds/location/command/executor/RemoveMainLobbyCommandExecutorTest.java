package nl.matsgemmeke.battlegrounds.location.command.executor;

import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveMainLobbyCommandExecutorTest {

    private static final TextTemplate MAIN_LOBBY_REMOVE_TEXT_TEMPLATE = LanguageFixture.getTextTemplate(TranslationKey.MAIN_LOBBY_REMOVE.getPath());

    @Mock
    private LocationConfiguration locationConfiguration;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private RemoveMainLobbyCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute removes main lobby location from configuration and sends confirmation messsage")
    void execute() {
        when(translator.translate(TranslationKey.MAIN_LOBBY_REMOVE.getPath())).thenReturn(MAIN_LOBBY_REMOVE_TEXT_TEMPLATE);

        commandExecutor.execute(player);

        verify(locationConfiguration).removeMainLobbyLocation();
        verify(player).sendMessage("&6You have successfully removed the main lobby location.");
    }
}
