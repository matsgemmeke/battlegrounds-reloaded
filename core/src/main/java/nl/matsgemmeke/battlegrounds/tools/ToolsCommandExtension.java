package nl.matsgemmeke.battlegrounds.tools;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

public class ToolsCommandExtension implements CommandExtension {

    private static final String SHOW_HITBOXES_COMMAND_USAGE = "/bg tools showhitboxes <seconds> <range>";
    private static final String SHOW_HITBOXES_COMMAND_SUGGESTION = "/bg tools showhitboxes ";
    private static final String[] SHOW_HITBOXES_COMMAND_PERMISSIONS = new String[] { "battlegrounds.tools.showhitboxes" };

    private final ToolsCommand toolsCommand;
    private final Translator translator;

    @Inject
    public ToolsCommandExtension(ToolsCommand toolsCommand, Translator translator) {
        this.toolsCommand = toolsCommand;
        this.translator = translator;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        String showHitboxesCommandDescription = translator.translate(TranslationKey.DESCRIPTION_SHOW_HITBOXES.getPath()).getText();

        CommandInfo showHitboxesCommandInfo = new CommandInfo(showHitboxesCommandDescription, SHOW_HITBOXES_COMMAND_USAGE, SHOW_HITBOXES_COMMAND_SUGGESTION, SHOW_HITBOXES_COMMAND_PERMISSIONS);

        toolsCommand.addCommandInfo(showHitboxesCommandInfo);

        commandManager.registerCommand(toolsCommand);
    }
}
