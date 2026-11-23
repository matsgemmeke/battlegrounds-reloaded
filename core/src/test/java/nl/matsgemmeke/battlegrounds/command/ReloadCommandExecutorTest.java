package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.Message;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReloadCommandExecutorTest {

    @Mock
    private BattlegroundsConfiguration config;
    @Mock
    private CommandSender sender;
    @Mock
    private Translator translator;
    @InjectMocks
    private ReloadCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute reloads config and sends confirmation message")
    void execute_successful() {
        Message reloadSuccessMessage = mock(Message.class);

        when(config.load()).thenReturn(true);
        when(translator.translateMessage(TranslationKey.RELOAD_SUCCESS.getPath())).thenReturn(Optional.of(reloadSuccessMessage));

        commandExecutor.execute(sender);

        verify(reloadSuccessMessage).send(sender);
    }

    @Test
    @DisplayName("execute sends error message when reloading fails")
    void execute_error() {
        Message reloadFailedMessage = mock(Message.class);

        when(config.load()).thenReturn(false);
        when(translator.translateMessage(TranslationKey.RELOAD_FAILED.getPath())).thenReturn(Optional.of(reloadFailedMessage));

        commandExecutor.execute(sender);

        verify(reloadFailedMessage).send(sender);
    }
}
