package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class HelpMenu {

    private static final String EMPTY_LINE = " ";

    private final Translator translator;

    @Inject
    public HelpMenu(Translator translator) {
        this.translator = translator;
    }

    public void sendHelpMenuAsNormalMessages(CommandSender sender, String title, List<CommandInfo> commandInfoList) {
        sender.sendMessage(EMPTY_LINE);
        sender.sendMessage(title);
        sender.sendMessage(EMPTY_LINE);

        for (CommandInfo commandInfo : commandInfoList) {
            String subcommandText = this.getCommandInfoText(commandInfo);

            sender.sendMessage(subcommandText);
        }

        sender.sendMessage(EMPTY_LINE);
    }

    public void sendHelpMenuAsJsonMessages(Player player, String title, List<CommandInfo> commandInfoList) {
        player.sendMessage(EMPTY_LINE);
        player.sendMessage(title);
        player.sendMessage(EMPTY_LINE);

        for (CommandInfo commandInfo : commandInfoList) {
            String subcommandText = this.getCommandInfoText(commandInfo);

            TextComponent message = new TextComponent(subcommandText);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandInfo.suggestion()));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(commandInfo.description())));

            player.spigot().sendMessage(message);
        }

        player.sendMessage(EMPTY_LINE);
    }

    private String getCommandInfoText(CommandInfo commandInfo) {
        Map<String, Object> values = Map.of(
                "bg_description", commandInfo.description(),
                "bg_usage", commandInfo.usage()
        );

        return translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath()).replace(values);
    }
}
