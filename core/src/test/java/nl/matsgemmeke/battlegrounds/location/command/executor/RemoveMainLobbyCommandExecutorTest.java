package nl.matsgemmeke.battlegrounds.location.command.executor;

import nl.matsgemmeke.battlegrounds.i18n.Message;
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

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveMainLobbyCommandExecutorTest {

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
        Message mainLobbyRemoveMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.MAIN_LOBBY_REMOVE.getPath())).thenReturn(Optional.of(mainLobbyRemoveMessage));

        commandExecutor.execute(player);

        verify(locationConfiguration).removeMainLobbyLocation();
        verify(mainLobbyRemoveMessage).send(player);
    }
}
