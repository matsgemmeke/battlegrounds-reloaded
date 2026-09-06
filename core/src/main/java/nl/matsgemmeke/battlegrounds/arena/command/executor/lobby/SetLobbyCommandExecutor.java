package nl.matsgemmeke.battlegrounds.arena.command.executor.lobby;

import org.bukkit.entity.Player;

public class SetLobbyCommandExecutor {

    public void execute(Player player, int arenaId) {
        player.sendMessage("setlobby" + arenaId);
    }
}
