package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.completion.MapNameCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.condition.NonexistentMapNameCondition;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

public class MapCommandExtension implements CommandExtension {

    private static final String CREATE_MAP_COMMAND_USAGE = "/bg arena map create <id> <name>";
    private static final String CREATE_MAP_COMMAND_SUGGESTION = "/bg arena map create ";
    private static final String[] CREATE_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.create" };

    private final MapCommand mapCommand;
    private final MapNameCommandCompletionHandler mapNameCommandCompletionHandler;
    private final NonexistentMapNameCondition nonexistentMapNameCondition;
    private final Translator translator;

    @Inject
    public MapCommandExtension(
            MapCommand mapCommand,
            MapNameCommandCompletionHandler mapNameCommandCompletionHandler,
            NonexistentMapNameCondition nonexistentMapNameCondition,
            Translator translator
    ) {
        this.mapCommand = mapCommand;
        this.mapNameCommandCompletionHandler = mapNameCommandCompletionHandler;
        this.nonexistentMapNameCondition = nonexistentMapNameCondition;
        this.translator = translator;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        String createMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATE_MAP.getPath()).getText();

        CommandInfo createMapCommandInfo = new CommandInfo(createMapCommandDescription, CREATE_MAP_COMMAND_USAGE, CREATE_MAP_COMMAND_SUGGESTION, CREATE_MAP_COMMAND_PERMISSIONS);

        mapCommand.addCommandInfo(createMapCommandInfo);

        commandManager.registerCommand(mapCommand);

        commandManager.getCommandCompletions().registerCompletion("map-name", mapNameCommandCompletionHandler);
        commandManager.getCommandConditions().addCondition(String.class, "nonexistent-map-name", nonexistentMapNameCondition);
    }
}
