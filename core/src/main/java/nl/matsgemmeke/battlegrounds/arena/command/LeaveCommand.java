package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.executor.LeaveCommandExecutor;
import org.bukkit.entity.Player;

@CommandAlias("battlegrounds|bg|battle")
public class LeaveCommand extends BaseCommand {

    private final LeaveCommandExecutor leaveCommandExecutor;

    @Inject
    public LeaveCommand(LeaveCommandExecutor leaveCommandExecutor) {
        this.leaveCommandExecutor = leaveCommandExecutor;
    }

    @Subcommand("leave")
    @Conditions("arena-mode-presence")
    @CommandPermission("battlegrounds.leave")
    public void onLeave(Player player) {
        leaveCommandExecutor.execute(player);
    }
}
