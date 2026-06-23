package nl.matsgemmeke.battlegrounds.command.arena;

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
@Subcommand("arena")
public class ArenaCommand extends BaseCommand {

    private final CreateArenaCommandExecutor createArenaCommandExecutor;
    private final RemoveArenaCommandExecutor removeArenaCommandExecutor;
    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public ArenaCommand(
            CreateArenaCommandExecutor createArenaCommandExecutor,
            RemoveArenaCommandExecutor removeArenaCommandExecutor,
            HelpMenu helpMenu,
            Translator translator
    ) {
        this.createArenaCommandExecutor = createArenaCommandExecutor;
        this.removeArenaCommandExecutor = removeArenaCommandExecutor;
        this.helpMenu = helpMenu;
        this.translator = translator;
        this.commandInfoList = new ArrayList<>();
    }

    public void addCommandInfo(CommandInfo commandInfo) {
        commandInfoList.add(commandInfo);
    }

    @Default
    public void onDefault(CommandSender sender) {
        String title = translator.translate(TranslationKey.ARENA_HELP_MENU_TITLE.getPath()).getText();

        if (sender instanceof Player player) {
            helpMenu.sendHelpMenuAsJsonMessages(player, title, commandInfoList);
        } else {
            helpMenu.sendHelpMenuAsNormalMessages(sender, title, commandInfoList);
        }
    }

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.createarena")
    @Subcommand("create")
    public void onCreate(CommandSender sender, @Conditions("nonexistent-arena-id") Integer id) {
        createArenaCommandExecutor.execute(sender, id);
    }

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.removearena")
    @Subcommand("remove")
    public void onRemove(CommandSender sender, @Conditions("existent-arena-id") Integer id) {
        removeArenaCommandExecutor.execute(sender, id);
    }
}
