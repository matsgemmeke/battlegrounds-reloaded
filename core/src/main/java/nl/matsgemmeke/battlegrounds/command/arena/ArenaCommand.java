package nl.matsgemmeke.battlegrounds.command.arena;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CreateArenaCommand;
import nl.matsgemmeke.battlegrounds.command.RemoveArenaCommand;
import org.bukkit.command.CommandSender;

@CommandAlias("battlegrounds|bg|battle")
@Subcommand("arena")
public class ArenaCommand extends BaseCommand {

    private final CreateArenaCommand createArenaExecutor;
    private final RemoveArenaCommand removeArenaExecutor;

    @Inject
    public ArenaCommand(CreateArenaCommand createArenaExecutor, RemoveArenaCommand removeArenaExecutor) {
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
