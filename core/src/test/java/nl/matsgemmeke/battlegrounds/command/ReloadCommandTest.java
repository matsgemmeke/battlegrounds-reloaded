package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReloadCommandTest {

    private static final String MESSAGE = "test";

    @Mock
    private BattlegroundsConfiguration config;
    @Mock
    private CommandSender sender;
    @Mock
    private Translator translator;

    private ReloadCommand reloadCommand;

    @BeforeEach
    public void setUp() {
        when(translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath())).thenReturn(new TextTemplate("description"));

        reloadCommand = new ReloadCommand(config, translator);
    }

    @Test
    @DisplayName("execute reloads config and sends confirmation message")
    void execute_successful() {
        when(config.load()).thenReturn(true);
        when(translator.translate(TranslationKey.RELOAD_SUCCESS.getPath())).thenReturn(new TextTemplate(MESSAGE));

        reloadCommand.execute(sender);

        verify(sender).sendMessage(MESSAGE);
    }

    @Test
    @DisplayName("execute sends error message when reloading fails")
    void execute_error() {
        when(config.load()).thenReturn(false);
        when(translator.translate(TranslationKey.RELOAD_FAILED.getPath())).thenReturn(new TextTemplate(MESSAGE));

        reloadCommand.execute(sender);

        verify(sender).sendMessage(MESSAGE);
    }
}
