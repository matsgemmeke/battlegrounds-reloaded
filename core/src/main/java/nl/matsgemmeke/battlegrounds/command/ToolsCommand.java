package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.command.tool.ShowHitboxesTool;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("battlegrounds|bg|battle")
@CommandPermission("battlegrounds.tools")
@Subcommand("tools")
public class ToolsCommand extends BaseCommand {

    private final ShowHitboxesTool showHitboxesTool;
    private final Translator translator;

    @Inject
    public ToolsCommand(ShowHitboxesTool showHitboxesTool, Translator translator) {
        this.showHitboxesTool = showHitboxesTool;
        this.translator = translator;
    }

    @Default
    public void onDefault(CommandSender sender, String[] args) {
        // With ACF we can only use CatchUnknown on root commands, so we catch unknown tool names in the onDefault
        if (args.length > 0 ) {
            Map<String, Object> values = Map.of("bg_tool", args[0]);
            sender.sendMessage(translator.translate(TranslationKey.TOOL_NOT_EXISTS.getPath()).replace(values));
            return;
        }

        String hitboxMessage = translator.translate(TranslationKey.TOOLS_MENU_COMMAND.getPath()).getText();
        String hitboxDescription = translator.translate(TranslationKey.DESCRIPTION_TOOLS_HITBOX.getPath()).getText();

        sender.sendMessage(" ");
        sender.sendMessage(translator.translate(TranslationKey.TOOLS_MENU_TITLE.getPath()).getText());
        sender.sendMessage(" ");

        if (sender instanceof Player player) {
            TextComponent message = new TextComponent(hitboxMessage);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bg tools hitbox"));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hitboxDescription)));

            player.spigot().sendMessage(message);
        } else {
            sender.sendMessage(hitboxMessage);
        }

        sender.sendMessage(" ");
    }

    @Subcommand("hitbox")
    @CommandCompletion("<seconds> <range>")
    @CommandPermission("battlegrounds.tools.hitbox")
    public void onHitbox(Player player, int seconds, @Default("10.0") double range) {
        showHitboxesTool.execute(player, seconds, range);
    }
}
