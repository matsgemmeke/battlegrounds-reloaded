package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.executor.JoinCommandExecutor;
import org.bukkit.entity.Player;

@CommandAlias("battlegrounds|bg|battle")
public class JoinCommand extends BaseCommand {

    private final JoinCommandExecutor joinCommandExecutor;

    @Inject
    public JoinCommand(JoinCommandExecutor joinCommandExecutor) {
        this.joinCommandExecutor = joinCommandExecutor;
    }

    @Subcommand("join")
    @Conditions("arena-absence")
    @CommandCompletion("@arena-id")
    @CommandPermission("battlegrounds.join")
    @Syntax("<arena>")
    public void onJoin(Player player, @Conditions("existent-arena-id") Integer arenaId) {
        joinCommandExecutor.execute(player, arenaId);
    }
}
