package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelpMenu {

    private final Translator translator;

    @Inject
    public HelpMenu(Translator translator) {
        this.translator = translator;
    }

    public void send(Audience audience, List<CommandInfo> commandInfoList) {
        audience.forEachAudience(member -> commandInfoList.stream()
                .filter(commandInfo -> this.hasPermission(member, commandInfo))
                .forEach(commandInfo -> this.send(member, commandInfo)));
    }

    private boolean hasPermission(Audience audience, CommandInfo commandInfo) {
        String[] permissions = commandInfo.permissions();

        if (permissions.length == 0) {
            return true;
        }

        return audience instanceof Permissible permissible
                && Arrays.stream(permissions).anyMatch(permissible::hasPermission);
    }

    private void send(Audience audience, CommandInfo commandInfo) {
        Replacement[] replacements = {
                Replacement.unparsed("bg-description", commandInfo.description()),
                Replacement.unparsed("bg-usage", commandInfo.usage()),
                Replacement.parsed("bg-suggestion", commandInfo.suggestion())
        };

        translator.translateMessage(TranslationKey.HELP_MENU_COMMAND.getPath(), replacements).ifPresent(message -> message.send(audience));
    }

    @Deprecated
    public void sendHelpMenuAsNormalMessages(CommandSender sender, List<CommandInfo> commandInfoList) {
        for (CommandInfo commandInfo : commandInfoList) {
            boolean permitted = Arrays.stream(commandInfo.permissions()).anyMatch(sender::hasPermission);

            if (permitted) {
                String subcommandText = this.getCommandInfoText(commandInfo);

                sender.sendMessage(subcommandText);
            }
        }
    }

    @Deprecated
    public void sendHelpMenuAsJsonMessages(Player player, List<CommandInfo> commandInfoList) {
        for (CommandInfo commandInfo : commandInfoList) {
            boolean permitted = Arrays.stream(commandInfo.permissions()).anyMatch(player::hasPermission);

            if (permitted) {
                String subcommandText = this.getCommandInfoText(commandInfo);

                TextComponent message = new TextComponent(subcommandText);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandInfo.suggestion()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(commandInfo.description())));

                player.spigot().sendMessage(message);
            }
        }
    }

    private String getCommandInfoText(CommandInfo commandInfo) {
        Map<String, Object> values = Map.of(
                "bg_description", commandInfo.description(),
                "bg_usage", commandInfo.usage()
        );

        return translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath()).replace(values);
    }
}
