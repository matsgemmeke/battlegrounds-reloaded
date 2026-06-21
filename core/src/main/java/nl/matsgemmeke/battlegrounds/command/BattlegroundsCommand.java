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

import java.util.LinkedHashMap;
import java.util.Map;

@CommandAlias("battlegrounds|bg|battle")
public class BattlegroundsCommand extends BaseCommand {

    private static final String EMPTY_MESSAGE = " ";

    private final Map<CommandInfo, Object> subcommands;
    private final Translator translator;

    @Inject
    public BattlegroundsCommand(Translator translator) {
        this.translator = translator;
        this.subcommands = new LinkedHashMap<>();
    }

    public void addSubcommand(CommandInfo commandInfo, Object subcommand) {
        subcommands.put(commandInfo, subcommand);
    }

    @Default
    @HelpCommand
    public void onDefault(CommandSender sender) {
        sender.sendMessage(EMPTY_MESSAGE);
        sender.sendMessage(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath()).getText());
        sender.sendMessage(EMPTY_MESSAGE);

        if (sender instanceof Player player) {
            for (CommandInfo commandInfo : subcommands.keySet()) {
                String subcommandText = this.getCommandInfoText(commandInfo);

                TextComponent message = new TextComponent(subcommandText);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandInfo.suggestion()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(commandInfo.description())));

                player.spigot().sendMessage(message);
            }
        } else {
            for (CommandInfo commandInfo : subcommands.keySet()) {
                String subcommandText = this.getCommandInfoText(commandInfo);

                sender.sendMessage(subcommandText);
            }
        }

        sender.sendMessage(EMPTY_MESSAGE);
    }

    private String getCommandInfoText(CommandInfo commandInfo) {
        Map<String, Object> values = Map.of(
                "bg_name", commandInfo.name(),
                "bg_description", commandInfo.description(),
                "bg_usage", commandInfo.usage()
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

    @SuppressWarnings("unchecked")
    private <T> T getSubcommand(String name) {
        return subcommands.entrySet().stream()
                .filter(entry -> entry.getKey().name().equalsIgnoreCase(name))
                .map(entry -> (T) entry.getValue())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unable to find a subcommand by the name " + name));
    }
}
