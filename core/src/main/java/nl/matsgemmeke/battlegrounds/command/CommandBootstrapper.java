package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;

import java.util.Set;

public class CommandBootstrapper {

    private final PaperCommandManager commandManager;
    private final Set<CommandExtension> commandExtensions;

    @Inject
    public CommandBootstrapper(PaperCommandManager commandManager, Set<CommandExtension> commandExtensions) {
        this.commandManager = commandManager;
        this.commandExtensions = commandExtensions;
    }

    public void initialize() {
        commandExtensions.forEach(extension -> extension.configure(commandManager));
    }
}
