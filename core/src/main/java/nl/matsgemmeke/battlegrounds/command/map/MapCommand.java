package nl.matsgemmeke.battlegrounds.command.map;

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

@CommandAlias("battlegrounds|bg|battle")
@CommandPermission("battlegrounds.map")
@Subcommand("arena map")
public class MapCommand extends BaseCommand {

    private final CreateMapCommandExecutor createMapCommandExecutor;
    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public MapCommand(CreateMapCommandExecutor createMapCommandExecutor, HelpMenu helpMenu, Translator translator) {
        this.createMapCommandExecutor = createMapCommandExecutor;
        this.helpMenu = helpMenu;
        this.translator = translator;
        this.commandInfoList = new ArrayList<>();
    }

    public void addCommandInfo(CommandInfo commandInfo) {
        commandInfoList.add(commandInfo);
    }

    @Default
    public void onDefault(CommandSender sender, String[] args) {
        if (args != null && args.length > 0) {
            sender.sendMessage(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath()).getText());
            return;
        }

        String title = translator.translate(TranslationKey.MAP_HELP_MENU_TITLE.getPath()).getText();

        if (sender instanceof Player player) {
            helpMenu.sendHelpMenuAsJsonMessages(player, title, commandInfoList);
        } else {
            helpMenu.sendHelpMenuAsNormalMessages(sender, title, commandInfoList);
        }
    }

    @CommandCompletion("<id> <name>")
    @CommandPermission("battlegrounds.map.create")
    @Subcommand("create")
    public void onCreate(CommandSender sender, @Conditions("existent-arena-id") Integer arenaId, String mapName) {
        createMapCommandExecutor.execute(sender, arenaId, mapName);
    }
}
