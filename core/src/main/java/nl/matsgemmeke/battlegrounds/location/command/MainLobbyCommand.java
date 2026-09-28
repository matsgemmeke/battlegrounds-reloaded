package nl.matsgemmeke.battlegrounds.location.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.command.executor.RemoveMainLobbyCommandExecutor;
import nl.matsgemmeke.battlegrounds.location.command.executor.SetMainLobbyCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Subcommand("mainlobby")
@CommandAlias("battlegrounds|bg|battle")
public class MainLobbyCommand extends BaseCommand {

    private final RemoveMainLobbyCommandExecutor removeMainLobbyCommandExecutor;
    private final SetMainLobbyCommandExecutor setMainLobbyCommandExecutor;
    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public MainLobbyCommand(
            RemoveMainLobbyCommandExecutor removeMainLobbyCommandExecutor,
            SetMainLobbyCommandExecutor setMainLobbyCommandExecutor,
            HelpMenu helpMenu,
            Translator translator
    ) {
        this.removeMainLobbyCommandExecutor = removeMainLobbyCommandExecutor;
        this.setMainLobbyCommandExecutor = setMainLobbyCommandExecutor;
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

        sender.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_HELP_MENU_HEADER.getPath()).getText());

        if (sender instanceof Player player) {
            helpMenu.sendHelpMenuAsJsonMessages(player, commandInfoList);
        } else {
            helpMenu.sendHelpMenuAsNormalMessages(sender, commandInfoList);
        }

        sender.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_HELP_MENU_FOOTER.getPath()).getText());
    }

    @Subcommand("remove")
    @Conditions("existent-main-lobby")
    @CommandPermission("battlegrounds.mainlobby.remove")
    public void onRemove(Player player) {
        removeMainLobbyCommandExecutor.execute(player);
    }

    @Subcommand("set")
    @CommandPermission("battlegrounds.mainlobby.set")
    public void onSet(Player player) {
        setMainLobbyCommandExecutor.execute(player);
    }
}
