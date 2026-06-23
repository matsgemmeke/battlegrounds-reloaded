package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("battlegrounds|bg|battle")
public class BattlegroundsCommand extends BaseCommand {

    private final GiveWeaponCommandExecutor giveWeaponCommandExecutor;
    private final ReloadCommandExecutor reloadCommandExecutor;
    private final SetMainLobbyCommandExecutor setMainLobbyCommandExecutor;
    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public BattlegroundsCommand(
            GiveWeaponCommandExecutor giveWeaponCommandExecutor,
            ReloadCommandExecutor reloadCommandExecutor,
            SetMainLobbyCommandExecutor setMainLobbyCommandExecutor,
            HelpMenu helpMenu,
            Translator translator
    ) {
        this.giveWeaponCommandExecutor = giveWeaponCommandExecutor;
        this.reloadCommandExecutor = reloadCommandExecutor;
        this.setMainLobbyCommandExecutor = setMainLobbyCommandExecutor;
        this.helpMenu = helpMenu;
        this.translator = translator;
        this.commandInfoList = new ArrayList<>();
    }

    public void addCommandInfo(CommandInfo commandInfo) {
        commandInfoList.add(commandInfo);
    }

    @Default
    @HelpCommand
    public void onDefault(CommandSender sender) {
        String title = translator.translate(TranslationKey.BATTLEGROUNDS_HELP_MENU_TITLE.getPath()).getText();

        if (sender instanceof Player player) {
            helpMenu.sendHelpMenuAsJsonMessages(player, title, commandInfoList);
        } else {
            helpMenu.sendHelpMenuAsNormalMessages(sender, title, commandInfoList);
        }
    }

    @CatchUnknown
    public void onCatchUnknown(CommandSender sender) {
        sender.sendMessage(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath()).getText());
    }

    @CommandCompletion("<weapon>")
    @CommandPermission("battlegrounds.giveweapon")
    @Conditions("freeplay-mode-presence")
    @Subcommand("giveweapon")
    public void onGiveWeapon(Player player, String[] args) {
        giveWeaponCommandExecutor.execute(player, args);
    }

    @CommandPermission("battlegrounds.reload")
    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        reloadCommandExecutor.execute(sender);
    }

    @CommandPermission("battlegrounds.setmainlobby")
    @Subcommand("setmainlobby")
    public void onSetMainLobby(Player player) {
        setMainLobbyCommandExecutor.execute(player);
    }
}
