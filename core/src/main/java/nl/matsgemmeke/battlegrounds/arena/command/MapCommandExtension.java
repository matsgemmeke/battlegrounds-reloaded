package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.completion.MapNameCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.condition.ExistentMapNameCondition;
import nl.matsgemmeke.battlegrounds.arena.command.condition.NonexistentMapNameCondition;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

public class MapCommandExtension implements CommandExtension {

    private static final String CREATE_MAP_COMMAND_USAGE = "/bg arena map create <id> <name>";
    private static final String CREATE_MAP_COMMAND_SUGGESTION = "/bg arena map create ";
    private static final String[] CREATE_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.create" };

    private static final String LIST_COMMAND_USAGE = "/bg arena map list <id>";
    private static final String LIST_COMMAND_SUGGESTION = "/bg arena map list ";
    private static final String[] LIST_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.list" };

    private static final String REMOVE_MAP_COMMAND_USAGE = "/bg arena map remove <id> <name>";
    private static final String REMOVE_MAP_COMMAND_SUGGESTION = "/bg arena map remove ";
    private static final String[] REMOVE_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.remove" };

    private final MapCommand mapCommand;
    private final MapNameCommandCompletionHandler mapNameCommandCompletionHandler;
    private final ExistentMapNameCondition existentMapNameCondition;
    private final NonexistentMapNameCondition nonexistentMapNameCondition;
    private final Translator translator;

    @Inject
    public MapCommandExtension(
            MapCommand mapCommand,
            MapNameCommandCompletionHandler mapNameCommandCompletionHandler,
            ExistentMapNameCondition existentMapNameCondition,
            NonexistentMapNameCondition nonexistentMapNameCondition,
            Translator translator
    ) {
        this.mapCommand = mapCommand;
        this.mapNameCommandCompletionHandler = mapNameCommandCompletionHandler;
        this.existentMapNameCondition = existentMapNameCondition;
        this.nonexistentMapNameCondition = nonexistentMapNameCondition;
        this.translator = translator;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        String createMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATE_MAP.getPath()).getText();
        String listCommandDescription = translator.translate(TranslationKey.DESCRIPTION_MAP_LIST.getPath()).getText();
        String removeMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVE_MAP.getPath()).getText();

        CommandInfo createMapCommandInfo = new CommandInfo(createMapCommandDescription, CREATE_MAP_COMMAND_USAGE, CREATE_MAP_COMMAND_SUGGESTION, CREATE_MAP_COMMAND_PERMISSIONS);
        CommandInfo listCommandInfo = new CommandInfo(listCommandDescription, LIST_COMMAND_USAGE, LIST_COMMAND_SUGGESTION, LIST_COMMAND_PERMISSIONS);
        CommandInfo removeMapCommandInfo = new CommandInfo(removeMapCommandDescription, REMOVE_MAP_COMMAND_USAGE, REMOVE_MAP_COMMAND_SUGGESTION, REMOVE_MAP_COMMAND_PERMISSIONS);

        mapCommand.addCommandInfo(createMapCommandInfo);
        mapCommand.addCommandInfo(listCommandInfo);
        mapCommand.addCommandInfo(removeMapCommandInfo);

        commandManager.registerCommand(mapCommand);

        commandManager.getCommandCompletions().registerCompletion("map-name", mapNameCommandCompletionHandler);
        commandManager.getCommandConditions().addCondition(String.class, "existent-map-name", existentMapNameCondition);
        commandManager.getCommandConditions().addCondition(String.class, "nonexistent-map-name", nonexistentMapNameCondition);
    }
}
