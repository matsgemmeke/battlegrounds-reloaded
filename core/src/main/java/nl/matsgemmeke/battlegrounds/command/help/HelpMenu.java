package nl.matsgemmeke.battlegrounds.command.help;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpMenu {

    private final List<HelpMenuEntry> entries;
    @Nullable
    private String footer;
    @Nullable
    private String header;

    private HelpMenu() {
        this.entries = new ArrayList<>();
    }

    public static HelpMenu create() {
        return new HelpMenu();
    }

    public HelpMenu header(String header) {
        this.header = header;
        return this;
    }

    public HelpMenu entries(List<HelpMenuEntry> entries) {
        this.entries.addAll(entries);
        return this;
    }

    public HelpMenu footer(String footer) {
        this.footer = footer;
        return this;
    }

    public void send(CommandSender sender) {
        if (header != null) {
            sender.sendMessage(header);
        }

        for (HelpMenuEntry entry : entries) {
            boolean permitted = Arrays.stream(entry.permissions()).anyMatch(sender::hasPermission);

            if (permitted) {
                if (sender instanceof Player player) {
                    TextComponent message = new TextComponent(entry.messageText());
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, entry.suggestion()));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(entry.hoverText())));

                    player.spigot().sendMessage(message);
                } else {
                    sender.sendMessage(entry.messageText());
                }
            }
        }

        if (footer != null) {
            sender.sendMessage(footer);
        }
    }
}
