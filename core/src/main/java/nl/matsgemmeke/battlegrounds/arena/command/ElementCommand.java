package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.executor.element.AddSpawnPointCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Subcommand("element")
@CommandAlias("battlegrounds|bg|battle")
@CommandPermission("battlegrounds.element")
public class ElementCommand extends BaseCommand {

    private final AddSpawnPointCommandExecutor addSpawnPointCommandExecutor;
    private final HelpMenu helpMenu;
    private final List<CommandInfo> commandInfoList;
    private final Translator translator;

    @Inject
    public ElementCommand(AddSpawnPointCommandExecutor addSpawnPointCommandExecutor, HelpMenu helpMenu, Translator translator) {
        this.addSpawnPointCommandExecutor = addSpawnPointCommandExecutor;
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

        String header = translator.translate(TranslationKey.ELEMENT_HELP_MENU_HEADER.getPath()).getText();

        if (sender instanceof Player player) {
            helpMenu.sendHelpMenuAsJsonMessages(player, header, commandInfoList);
        } else {
            helpMenu.sendHelpMenuAsNormalMessages(sender, header, commandInfoList);
        }
    }

    @Subcommand("add")
    public void onAdd(Player player) {
        player.sendMessage(translator.translate(TranslationKey.SPECIFY_ELEMENT_TYPE.getPath()).getText());
    }

    @Subcommand("add spawnpoint")
    @Syntax("<team>")
    @Conditions("map-selected")
    @CommandPermission("battlegrounds.element.add")
    public void onAddSpawnPoint(Player player, @Default("1") Integer teamId) {
        addSpawnPointCommandExecutor.execute(player, teamId);
    }
}
