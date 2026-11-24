package nl.matsgemmeke.battlegrounds.tools;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("battlegrounds|bg|battle")
@CommandPermission("battlegrounds.tools")
@Subcommand("tools")
public class ToolsCommand extends BaseCommand {

    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final ShowHitboxesCommandExecutor showHitboxesCommandExecutor;
    private final Translator translator;

    @Inject
    public ToolsCommand(HelpMenu helpMenu, ShowHitboxesCommandExecutor showHitboxesCommandExecutor, Translator translator) {
        this.helpMenu = helpMenu;
        this.showHitboxesCommandExecutor = showHitboxesCommandExecutor;
        this.translator = translator;
        this.commandInfoList = new ArrayList<>();
    }

    public void addCommandInfo(CommandInfo commandInfo) {
        commandInfoList.add(commandInfo);
    }

    @Default
    public void onDefault(CommandSender sender, String[] args) {
        // With ACF we can only use CatchUnknown on root commands, so we catch unknown tool names in the onDefault
        if (args != null && args.length > 0 ) {
            Replacement toolReplacement = Replacement.parsed("tool", args[0]);

            translator.translateMessage(TranslationKey.TOOL_NOT_EXISTS.getPath(), toolReplacement).ifPresent(message -> message.send(sender));
            return;
        }

        translator.translateMessage(TranslationKey.TOOLS_HELP_MENU_HEADER.getPath()).ifPresent(message -> message.send(sender));

        helpMenu.send(sender, commandInfoList);

        translator.translateMessage(TranslationKey.TOOLS_HELP_MENU_FOOTER.getPath()).ifPresent(message -> message.send(sender));
    }

    @Subcommand("showhitboxes")
    @CommandCompletion("<seconds> <range>")
    @CommandPermission("battlegrounds.tools.showhitboxes")
    public void onShowHitboxes(Player player, @Default("10") int seconds, @Default("10.0") double range) {
        showHitboxesCommandExecutor.execute(player, seconds, range);
    }
}
