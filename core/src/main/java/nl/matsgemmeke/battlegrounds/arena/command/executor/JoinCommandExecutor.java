package nl.matsgemmeke.battlegrounds.arena.command.executor;

import org.bukkit.entity.Player;

public class JoinCommandExecutor {

    public void execute(Player player, int arenaId) {
        player.sendMessage("hi " + arenaId);
    }
}
