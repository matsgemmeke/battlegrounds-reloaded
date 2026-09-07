package nl.matsgemmeke.battlegrounds.arena.command.executor.element;

import org.bukkit.entity.Player;

public class RemoveElementCommandExecutor {

    public void execute(Player player, int elementId) {
        player.sendMessage("remove element " + elementId);
    }
}
