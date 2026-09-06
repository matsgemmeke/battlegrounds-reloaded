package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.executor.lobby.SetLobbyCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Subcommand("arena lobby")
@CommandAlias("battlegrounds|bg|battle")
@CommandPermission("battlegrounds.lobby")
public class LobbyCommand extends BaseCommand {

    private final SetLobbyCommandExecutor setLobbyCommandExecutor;
    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public LobbyCommand(SetLobbyCommandExecutor setLobbyCommandExecutor, HelpMenu helpMenu, Translator translator) {
        this.setLobbyCommandExecutor = setLobbyCommandExecutor;
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

        String header = translator.translate(TranslationKey.LOBBY_HELP_MENU_HEADER.getPath()).getText();

        if (sender instanceof Player player) {
            helpMenu.sendHelpMenuAsJsonMessages(player, header, commandInfoList);
        } else {
            helpMenu.sendHelpMenuAsNormalMessages(sender, header, commandInfoList);
        }
    }

    @Subcommand("set")
    @Syntax("<arena>")
    @CommandCompletion("@arena-id")
    @CommandPermission("battlegrounds.lobby.set")
    public void onSet(Player player, @Conditions("existent-arena-id") @Name("arena-id") Integer arenaId) {
        setLobbyCommandExecutor.execute(player, arenaId);
    }
}
