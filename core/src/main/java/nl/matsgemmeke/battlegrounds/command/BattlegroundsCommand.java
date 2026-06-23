package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@CommandAlias("battlegrounds|bg|battle")
public class BattlegroundsCommand extends BaseCommand {

    private final HelpMenu helpMenu;
    private final Map<CommandInfo, Object> subcommands;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public BattlegroundsCommand(HelpMenu helpMenu, Translator translator) {
        this.helpMenu = helpMenu;
        this.translator = translator;
        this.commandInfoList = new ArrayList<>();
        this.subcommands = new LinkedHashMap<>();
    }

    public void addCommandInfo(CommandInfo commandInfo) {
        commandInfoList.add(commandInfo);
    }

    public void addSubcommand(CommandInfo commandInfo, Object subcommand) {
        subcommands.put(commandInfo, subcommand);
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
        GiveWeaponCommand command = this.getSubcommand("giveweapon");
        command.execute(player, args);
    }

    @CommandPermission("battlegrounds.reload")
    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        ReloadCommand command = this.getSubcommand("reload");
        command.execute(sender);
    }

    @CommandPermission("battlegrounds.setmainlobby")
    @Subcommand("setmainlobby")
    public void onSetMainLobby(Player player) {
        SetMainLobbyCommand command = this.getSubcommand("setmainlobby");
        command.execute(player);
    }

    @SuppressWarnings("unchecked")
    private <T> T getSubcommand(String name) {
        return null;
    }
}
