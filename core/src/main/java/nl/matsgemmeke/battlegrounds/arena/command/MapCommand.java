package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.executor.CreateMapCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.RemoveMapCommandExecutor;
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
    private final RemoveMapCommandExecutor removeMapCommandExecutor;
    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public MapCommand(
            CreateMapCommandExecutor createMapCommandExecutor,
            RemoveMapCommandExecutor removeMapCommandExecutor,
            HelpMenu helpMenu,
            Translator translator) {
        this.createMapCommandExecutor = createMapCommandExecutor;
        this.removeMapCommandExecutor = removeMapCommandExecutor;
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

    @CommandCompletion("@arena-id @nothing")
    @CommandPermission("battlegrounds.map.create")
    @Subcommand("create")
    public void onCreate(Player player, @Conditions("existent-arena-id") @Name("arena-id") Integer arenaId, @Conditions("nonexistent-map-name") String mapName) {
        createMapCommandExecutor.execute(player, arenaId, mapName);
    }

    // NOTE: The trailing "@nothing" in @CommandCompletion is intentional! The param mapName is a greedy String, so at
    // runtime ACF joins all remaining args into one value (e.g. "My Map") - execution works fine without it. But for
    // command completions, ACF has no concept of "multi-word argument" - it just reuses the last declared completer
    // for any extra arguments. Without "@nothing", the @map-name suggestions would keep popping up after every space,
    // as if starting a new argument. With "@nothing" the command will not suggest map names once past arena-id + 1st
    // word of the map.
    //
    // Downside: no tab-complete help for the 2nd+ word of a map name (e.g. typing "My " won't suggest anything) - but
    // manual typing still parses correctly.
    @CommandCompletion("@arena-id @map-name @nothing")
    @CommandPermission("battlegrounds.map.remove")
    @Subcommand("remove")
    public void onRemove(Player player, @Conditions("existent-arena-id") @Name("arena-id") Integer arenaId, @Conditions("existent-map-name") String mapName) {
        removeMapCommandExecutor.execute(player, arenaId, mapName);
    }
}
