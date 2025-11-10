package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.command.tool.ShowHitboxesTool;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolsCommandTest {

    @Mock
    private ShowHitboxesTool showHitboxesTool;
    @Mock
    private Translator translator;
    @InjectMocks
    private ToolsCommand command;

    @Test
    void onDefaultSendsErrorMessageToCommandSenderWhenArgsAreDefined() {
        CommandSender commandSender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.TOOL_NOT_EXISTS.getPath())).thenReturn(new TextTemplate("&cA tool by the name of %bg_tool% doesn't exist."));

        command.onDefault(commandSender, new String[] { "test" });

        verify(commandSender).sendMessage("&cA tool by the name of test doesn't exist.");
    }

    @Test
    void onHitboxExecutesShowHitboxesTool() {
        Player player = mock(Player.class);

        command.onHitbox(player, 10, 20.0);

        verify(showHitboxesTool).execute(player, 10, 20.0);
    }
}
