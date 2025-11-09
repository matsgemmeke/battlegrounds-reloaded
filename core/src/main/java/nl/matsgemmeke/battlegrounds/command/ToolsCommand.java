package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("battlegrounds|bg|battle")
@Subcommand("tools")
public class ToolsCommand extends BaseCommand {

    private final Translator translator;

    @Inject
    public ToolsCommand(Translator translator) {
        this.translator = translator;
    }

    @Default
    public void onDefault(CommandSender sender, String[] args) {
        sender.sendMessage(translator.translate(TranslationKey.TOOLS_MENU_TITLE.getPath()).getText());
        sender.sendMessage(" ");

        if (sender instanceof Player player) {
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

    @Subcommand("hitbox|hitboxes|showhitbox|showhitboxes")
    public void onShowHitbox(CommandSender sender, String[] args) {
        // logic here
    }
}
