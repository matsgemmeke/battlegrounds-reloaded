package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.completion.ArenaIdCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.NonexistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

public class ArenaCommandExtension implements CommandExtension {

    private static final String CREATE_ARENA_COMMAND_USAGE = "/bg arena create <id>";
    private static final String CREATE_ARENA_COMMAND_SUGGESTION = "/bg arena create ";
    private static final String[] CREATE_ARENA_COMMAND_PERMISSIONS = new String[] { "battlegrounds.arena.create" };

    private static final String REMOVE_ARENA_COMMAND_USAGE = "/bg arena remove <id>";
    private static final String REMOVE_ARENA_COMMAND_SUGGESTION = "/bg arena remove ";
    private static final String[] REMOVE_ARENA_COMMAND_PERMISSIONS = new String[] { "battlegrounds.arena.remove" };

    private final ArenaCommand arenaCommand;
    private final ArenaIdCommandCompletionHandler arenaIdCommandCompletionHandler;
    private final ExistentArenaIdCondition existentArenaIdCondition;
    private final NonexistentArenaIdCondition nonexistentArenaIdCondition;
    private final Translator translator;

    @Inject
    public ArenaCommandExtension(
            ArenaCommand arenaCommand,
            ArenaIdCommandCompletionHandler arenaIdCommandCompletionHandler,
            ExistentArenaIdCondition existentArenaIdCondition,
            NonexistentArenaIdCondition nonexistentArenaIdCondition,
            Translator translator
    ) {
        this.arenaCommand = arenaCommand;
        this.arenaIdCommandCompletionHandler = arenaIdCommandCompletionHandler;
        this.existentArenaIdCondition = existentArenaIdCondition;
        this.nonexistentArenaIdCondition = nonexistentArenaIdCondition;
        this.translator = translator;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        String createArenaDescription = translator.translate(TranslationKey.DESCRIPTION_CREATE_ARENA.getPath()).getText();
        String removeArenaDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVE_ARENA.getPath()).getText();

        arenaCommand.addCommandInfo(new CommandInfo(createArenaDescription, CREATE_ARENA_COMMAND_USAGE, CREATE_ARENA_COMMAND_SUGGESTION, CREATE_ARENA_COMMAND_PERMISSIONS));
        arenaCommand.addCommandInfo(new CommandInfo(removeArenaDescription, REMOVE_ARENA_COMMAND_USAGE, REMOVE_ARENA_COMMAND_SUGGESTION, REMOVE_ARENA_COMMAND_PERMISSIONS));

        commandManager.registerCommand(arenaCommand);

        commandManager.getCommandCompletions().registerCompletion("arena-id", arenaIdCommandCompletionHandler);
        commandManager.getCommandConditions().addCondition(Integer.class, "existent-arena-id", existentArenaIdCondition);
        commandManager.getCommandConditions().addCondition(Integer.class, "nonexistent-arena-id", nonexistentArenaIdCondition);
    }
}
