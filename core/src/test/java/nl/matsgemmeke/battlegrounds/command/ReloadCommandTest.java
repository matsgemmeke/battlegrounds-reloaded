package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ReloadCommandTest {

    private BattlegroundsConfiguration config;
    private CommandSender sender;
    private Translator translator;

    @Before
    public void setUp() {
        this.config = mock(BattlegroundsConfiguration.class);
        this.sender = mock(CommandSender.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath())).thenReturn("description");
    }

    @Test
    public void shouldReloadConfigsOnRunningReloadCommand() {
        String message = "hello";

        when(config.load()).thenReturn(true);
        when(translator.translate(TranslationKey.RELOAD_SUCCESS.getPath())).thenReturn(message);

        ReloadCommand reloadCommand = new ReloadCommand(config, translator);
        reloadCommand.execute(sender);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldSendMessageUponErrorOnRunningReloadCommand() {
        String message = "hello";

        when(config.load()).thenReturn(false);
        when(translator.translate(TranslationKey.RELOAD_FAILED.getPath())).thenReturn(message);

        ReloadCommand reloadCommand = new ReloadCommand(config, translator);
        reloadCommand.execute(sender);

        verify(sender).sendMessage(message);
    }
}
