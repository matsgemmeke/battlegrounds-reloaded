package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.completion.MapNameCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.condition.ExistentMapNameCondition;
import nl.matsgemmeke.battlegrounds.arena.command.condition.MapSelectedCondition;
import nl.matsgemmeke.battlegrounds.arena.command.condition.NonexistentMapNameCondition;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

public class MapCommandExtension implements CommandExtension {

    private static final String CREATE_MAP_COMMAND_USAGE = "/bg arena map create <id> <name>";
    private static final String CREATE_MAP_COMMAND_SUGGESTION = "/bg arena map create ";
    private static final String[] CREATE_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.create" };

    private static final String REMOVE_MAP_COMMAND_USAGE = "/bg arena map remove <id> <name>";
    private static final String REMOVE_MAP_COMMAND_SUGGESTION = "/bg arena map remove ";
    private static final String[] REMOVE_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.remove" };

    private static final String SELECT_COMMAND_USAGE = "/bg arena map select <id> <name>";
    private static final String SELECT_COMMAND_SUGGESTION = "/bg arena map select ";
    private static final String[] SELECT_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.select" };

    private final MapCommand mapCommand;
    private final MapNameCommandCompletionHandler mapNameCommandCompletionHandler;
    private final ExistentMapNameCondition existentMapNameCondition;
    private final NonexistentMapNameCondition nonexistentMapNameCondition;
    private final MapSelectedCondition mapSelectedCondition;
    private final Translator translator;

    @Inject
    public MapCommandExtension(
            MapCommand mapCommand,
            MapNameCommandCompletionHandler mapNameCommandCompletionHandler,
            ExistentMapNameCondition existentMapNameCondition,
            NonexistentMapNameCondition nonexistentMapNameCondition,
            MapSelectedCondition mapSelectedCondition,
            Translator translator
    ) {
        this.mapCommand = mapCommand;
        this.mapNameCommandCompletionHandler = mapNameCommandCompletionHandler;
        this.existentMapNameCondition = existentMapNameCondition;
        this.nonexistentMapNameCondition = nonexistentMapNameCondition;
        this.mapSelectedCondition = mapSelectedCondition;
        this.translator = translator;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        String createMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATE_MAP.getPath()).getText();
        String removeMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVE_MAP.getPath()).getText();
        String selectCommandDescription = translator.translate(TranslationKey.DESCRIPTION_MAP_SELECT.getPath()).getText();

        CommandInfo createMapCommandInfo = new CommandInfo(createMapCommandDescription, CREATE_MAP_COMMAND_USAGE, CREATE_MAP_COMMAND_SUGGESTION, CREATE_MAP_COMMAND_PERMISSIONS);
        CommandInfo removeMapCommandInfo = new CommandInfo(removeMapCommandDescription, REMOVE_MAP_COMMAND_USAGE, REMOVE_MAP_COMMAND_SUGGESTION, REMOVE_MAP_COMMAND_PERMISSIONS);
        CommandInfo selectCommandInfo = new CommandInfo(selectCommandDescription, SELECT_COMMAND_USAGE, SELECT_COMMAND_SUGGESTION, SELECT_COMMAND_PERMISSIONS);

        mapCommand.addCommandInfo(createMapCommandInfo);
        mapCommand.addCommandInfo(removeMapCommandInfo);
        mapCommand.addCommandInfo(selectCommandInfo);

        commandManager.registerCommand(mapCommand);

        commandManager.getCommandCompletions().registerCompletion("map-name", mapNameCommandCompletionHandler);
        commandManager.getCommandConditions().addCondition("map-selected", mapSelectedCondition);
        commandManager.getCommandConditions().addCondition(String.class, "existent-map-name", existentMapNameCondition);
        commandManager.getCommandConditions().addCondition(String.class, "nonexistent-map-name", nonexistentMapNameCondition);
    }
}
