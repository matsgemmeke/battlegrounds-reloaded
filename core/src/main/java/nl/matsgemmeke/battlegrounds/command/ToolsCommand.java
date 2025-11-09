package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.tool.DisplayHitboxesTool;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("battlegrounds|bg|battle")
@CommandPermission("battlegrounds.tools")
@Subcommand("tools")
public class ToolsCommand extends BaseCommand {

    private final DisplayHitboxesTool displayHitboxesTool;
    private final Translator translator;

    @Inject
    public ToolsCommand(DisplayHitboxesTool displayHitboxesTool, Translator translator) {
        this.displayHitboxesTool = displayHitboxesTool;
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

        sender.sendMessage(translator.translate(TranslationKey.TOOLS_MENU_TITLE.getPath()).getText());
        sender.sendMessage(" ");
        sender.sendMessage(" /bg tools hitbox - Show hitboxes of nearby entities");
        sender.sendMessage(" ");
    }

    @CommandPermission("battlegrounds.tools.hitbox")
    @Subcommand("hitbox")
    public void onHitbox(Player player) {
        displayHitboxesTool.execute(player);
    }
}
