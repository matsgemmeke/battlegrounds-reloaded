package nl.matsgemmeke.battlegrounds.arena.command.executor;

import org.bukkit.entity.Player;

public class SelectMapCommandExecutor {

    public void execute(Player player, int arenaId, String mapName) {
        player.sendMessage(arenaId + " " + mapName);
    }
}
