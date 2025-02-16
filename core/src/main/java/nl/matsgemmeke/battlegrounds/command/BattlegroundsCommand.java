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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("battlegrounds|bg|battle")
public class BattlegroundsCommand extends BaseCommand {

    private static final String EMPTY_MESSAGE = " ";

    @NotNull
    private final List<CommandSource> subcommands;
    @NotNull
    private final Translator translator;

    @Inject
    public BattlegroundsCommand(@NotNull Translator translator) {
        this.translator = translator;
        this.subcommands = new ArrayList<>();
    }

    public boolean addSubcommand(@NotNull CommandSource subcommand) {
        return subcommands.add(subcommand);
    }

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.createsession")
    @Subcommand("createsession|cs")
    public void onCreateSession(@NotNull CommandSender sender, @Conditions("nonexistent-session-id") Integer id) {
        CreateSessionCommand command = this.getSubcommand("createsession");
        command.execute(sender, id);
    }

    @Default
    public void onDefault(@NotNull CommandSender sender) {
        sender.sendMessage(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath()).getText());
        sender.sendMessage(EMPTY_MESSAGE);

        if (sender instanceof Player) {
            Player player = (Player) sender;

            for (CommandSource subcommand : subcommands) {
                TextComponent message = new TextComponent(subcommand.getUsage());
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, subcommand.getUsage()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(subcommand.getDescription())));

                player.spigot().sendMessage(message);
            }
        } else {
            for (CommandSource subcommand : subcommands) {
                sender.sendMessage(subcommand.getUsage());
            }
        }
    }

    @CommandCompletion("<weapon>")
    @CommandPermission("battlegrounds.giveweapon")
    @Conditions("training-mode-presence")
    @Subcommand("giveweapon")
    public void onGiveWeapon(@NotNull Player player, @Conditions("existent-weapon-id") String weaponId) {
        GiveWeaponCommand command = this.getSubcommand("giveweapon");
        command.execute(player, weaponId);
    }

    @CommandPermission("battlegrounds.reload")
    @Subcommand("reload")
    public void onReload(@NotNull CommandSender sender) {
        ReloadCommand command = this.getSubcommand("reload");
        command.execute(sender);
    }

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.removesession")
    @Subcommand("removesession")
    public void onRemoveSession(@NotNull CommandSender sender, @Conditions("existent-session-id") Integer id) {
        RemoveSessionCommand command = this.getSubcommand("removesession");
        command.execute(sender, id);
    }

    @CommandPermission("battlegrounds.setmainlobby")
    @Subcommand("setmainlobby")
    public void onSetMainLobby(@NotNull Player player) {
        SetMainLobbyCommand command = this.getSubcommand("setmainlobby");
        command.execute(player);
    }

    @NotNull
    private <T extends CommandSource> T getSubcommand(@NotNull String name) {
        for (CommandSource subcommand : subcommands) {
            if (subcommand.getName().equalsIgnoreCase(name)) {
                return (T) subcommand;
            }
        }
        throw new IllegalArgumentException("Unable to find a subcommand by the name " + name);
    }
}
