package nl.matsgemmeke.battlegrounds.command.map;

import org.bukkit.command.CommandSender;

public class CreateMapCommandExecutor {

    public void execute(CommandSender sender, int arenaId, String mapName) {
        sender.sendMessage(arenaId + " " + mapName);
    }
}
