package nl.matsgemmeke.battlegrounds.location.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;

public class LocationCommandExtension implements CommandExtension {

    private final MainLobbyCommand mainLobbyCommand;

    @Inject
    public LocationCommandExtension(MainLobbyCommand mainLobbyCommand) {
        this.mainLobbyCommand = mainLobbyCommand;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        commandManager.registerCommand(mainLobbyCommand);
    }
}
