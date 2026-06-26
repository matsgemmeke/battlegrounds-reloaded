package nl.matsgemmeke.battlegrounds.command.tools;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            Map<String, Object> values = Map.of("bg_tool", args[0]);
            sender.sendMessage(translator.translate(TranslationKey.TOOL_NOT_EXISTS.getPath()).replace(values));
            return;
        }

        String title = translator.translate(TranslationKey.TOOLS_HELP_MENU_TITLE.getPath()).getText();

        if (sender instanceof Player player) {
            helpMenu.sendHelpMenuAsJsonMessages(player, title, commandInfoList);
        } else {
            helpMenu.sendHelpMenuAsNormalMessages(sender, title, commandInfoList);
        }
    }

    @Subcommand("showhitboxes")
    @CommandCompletion("<seconds> <range>")
    @CommandPermission("battlegrounds.tools.showhitboxes")
    public void onShowHitboxes(Player player, @Default("10") int seconds, @Default("10.0") double range) {
        showHitboxesCommandExecutor.execute(player, seconds, range);
    }
}
