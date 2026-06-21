package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandAlias("battlegrounds|bg|battle")
public class BattlegroundsCommand extends BaseCommand {

    private static final String EMPTY_MESSAGE = " ";

    private final List<CommandSource> subcommands;
    private final Translator translator;

    @Inject
    public BattlegroundsCommand(Translator translator) {
        this.translator = translator;
        this.subcommands = new ArrayList<>();
    }

    public boolean addSubcommand(CommandSource subcommand) {
        return subcommands.add(subcommand);
    }

    @Default
    @HelpCommand
    public void onDefault(CommandSender sender) {
        sender.sendMessage(EMPTY_MESSAGE);
        sender.sendMessage(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath()).getText());
        sender.sendMessage(EMPTY_MESSAGE);

        if (sender instanceof Player player) {
            for (CommandSource subcommand : subcommands) {
                String subcommandText = this.getSubcommandText(subcommand);

                TextComponent message = new TextComponent(subcommandText);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + subcommand.getUsage()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(subcommand.getDescription())));

                player.spigot().sendMessage(message);
            }
        } else {
            for (CommandSource subcommand : subcommands) {
                String subcommandText = this.getSubcommandText(subcommand);

                sender.sendMessage(subcommandText);
            }
        }

        sender.sendMessage(EMPTY_MESSAGE);
    }

    private String getSubcommandText(CommandSource subcommand) {
        Map<String, Object> values = Map.of(
                "bg_name", subcommand.getName(),
                "bg_description", subcommand.getDescription(),
                "bg_usage", subcommand.getUsage()
        );

        return translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath()).replace(values);
    }

    @CatchUnknown
    public void onCatchUnknown(CommandSender sender) {
        sender.sendMessage(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath()).getText());
    }

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.createarena")
    @Subcommand("createarena|ca")
    public void onCreateArena(CommandSender sender, @Conditions("nonexistent-arena-id") Integer id) {
        CreateArenaCommand command = this.getSubcommand("createarena");
        command.execute(sender, id);
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

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.removearena")
    @Subcommand("removearena")
    public void onRemoveArena(CommandSender sender, @Conditions("existent-arena-id") Integer id) {
        RemoveArenaCommand command = this.getSubcommand("removearena");
        command.execute(sender, id);
    }

    @CommandPermission("battlegrounds.setmainlobby")
    @Subcommand("setmainlobby")
    public void onSetMainLobby(Player player) {
        SetMainLobbyCommand command = this.getSubcommand("setmainlobby");
        command.execute(player);
    }

    private <T extends CommandSource> T getSubcommand(String name) {
        for (CommandSource subcommand : subcommands) {
            if (subcommand.getName().equalsIgnoreCase(name)) {
                return (T) subcommand;
            }
        }

        throw new IllegalArgumentException("Unable to find a subcommand by the name " + name);
    }
}
