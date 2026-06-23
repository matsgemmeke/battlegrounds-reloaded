package nl.matsgemmeke.battlegrounds.command.arena;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import org.bukkit.command.CommandSender;

@CommandAlias("battlegrounds|bg|battle")
@Subcommand("arena")
public class ArenaCommand extends BaseCommand {

    private final CreateArenaExecutor createArenaExecutor;
    private final RemoveArenaExecutor removeArenaExecutor;

    @Inject
    public ArenaCommand(CreateArenaExecutor createArenaExecutor, RemoveArenaExecutor removeArenaExecutor) {
        this.createArenaExecutor = createArenaExecutor;
        this.removeArenaExecutor = removeArenaExecutor;
    }

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.createarena")
    @Subcommand("create")
    public void onCreate(CommandSender sender, @Conditions("nonexistent-arena-id") Integer id) {
        createArenaExecutor.execute(sender, id);
    }

    @CommandCompletion("<id>")
    @CommandPermission("battlegrounds.removearena")
    @Subcommand("remove")
    public void onRemove(CommandSender sender, @Conditions("existent-arena-id") Integer id) {
        removeArenaExecutor.execute(sender, id);
    }
}
