package nl.matsgemmeke.battlegrounds.location.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.location.command.executor.SetMainLobbyCommandExecutor;
import org.bukkit.entity.Player;

@Subcommand("mainlobby")
@CommandAlias("battlegrounds|bg|battle")
public class MainLobbyCommand extends BaseCommand {

    private final SetMainLobbyCommandExecutor setMainLobbyCommandExecutor;

    @Inject
    public MainLobbyCommand(SetMainLobbyCommandExecutor setMainLobbyCommandExecutor) {
        this.setMainLobbyCommandExecutor = setMainLobbyCommandExecutor;
    }

    @Subcommand("set")
    @CommandPermission("battlegrounds.mainlobby.set")
    public void onSet(Player player) {
        setMainLobbyCommandExecutor.execute(player);
    }
}
