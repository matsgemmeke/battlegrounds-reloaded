package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.command.completion.ArenaIdCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.completion.MapNameCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.condition.*;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

public class ArenaCommandExtension implements CommandExtension {

    private static final String CREATE_ARENA_COMMAND_USAGE = "/bg arena create <arena>";
    private static final String CREATE_ARENA_COMMAND_SUGGESTION = "/bg arena create ";
    private static final String[] CREATE_ARENA_COMMAND_PERMISSIONS = new String[] { "battlegrounds.arena.create" };

    private static final String MAP_COMMAND_USAGE = "/bg arena map";
    private static final String MAP_COMMAND_SUGGESTION = "/bg arena map";
    private static final String[] MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map" };

    private static final String REMOVE_ARENA_COMMAND_USAGE = "/bg arena remove <arena>";
    private static final String REMOVE_ARENA_COMMAND_SUGGESTION = "/bg arena remove ";
    private static final String[] REMOVE_ARENA_COMMAND_PERMISSIONS = new String[] { "battlegrounds.arena.remove" };

    private static final String CREATE_MAP_COMMAND_USAGE = "/bg arena map create <arena> <map>";
    private static final String CREATE_MAP_COMMAND_SUGGESTION = "/bg arena map create ";
    private static final String[] CREATE_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.create" };

    private static final String REMOVE_MAP_COMMAND_USAGE = "/bg arena map remove <arena> <map>";
    private static final String REMOVE_MAP_COMMAND_SUGGESTION = "/bg arena map remove ";
    private static final String[] REMOVE_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.remove" };

    private static final String SELECT_MAP_COMMAND_USAGE = "/bg arena map select <arena> <map>";
    private static final String SELECT_MAP_COMMAND_SUGGESTION = "/bg arena map select ";
    private static final String[] SELECT_MAP_COMMAND_PERMISSIONS = new String[] { "battlegrounds.map.select" };

    private static final String ADD_ELEMENT_COMMAND_USAGE = "/bg element add <type>";
    private static final String ADD_ELEMENT_COMMAND_SUGGESTION = "/bg element add ";
    private static final String[] ADD_ELEMENT_COMMAND_PERMISSIONS = new String[] { "battlegrounds.element.add" };

    private final ArenaCommand arenaCommand;
    private final ElementCommand elementCommand;
    private final JoinCommand joinCommand;
    private final MapCommand mapCommand;

    private final ArenaIdCommandCompletionHandler arenaIdCommandCompletionHandler;
    private final MapNameCommandCompletionHandler mapNameCommandCompletionHandler;

    private final ArenaModeAbsenceCondition arenaModeAbsenceCondition;
    private final ExistentArenaIdCondition existentArenaIdCondition;
    private final NonexistentArenaIdCondition nonexistentArenaIdCondition;
    private final ExistentMapNameCondition existentMapNameCondition;
    private final NonexistentMapNameCondition nonexistentMapNameCondition;
    private final MapSelectedCondition mapSelectedCondition;

    private final Translator translator;

    @Inject
    public ArenaCommandExtension(
            ArenaCommand arenaCommand,
            ElementCommand elementCommand,
            JoinCommand joinCommand,
            MapCommand mapCommand,
            ArenaIdCommandCompletionHandler arenaIdCommandCompletionHandler,
            MapNameCommandCompletionHandler mapNameCommandCompletionHandler,
            ArenaModeAbsenceCondition arenaModeAbsenceCondition,
            ExistentArenaIdCondition existentArenaIdCondition,
            NonexistentArenaIdCondition nonexistentArenaIdCondition,
            ExistentMapNameCondition existentMapNameCondition,
            NonexistentMapNameCondition nonexistentMapNameCondition,
            MapSelectedCondition mapSelectedCondition,
            Translator translator
    ) {
        this.arenaCommand = arenaCommand;
        this.elementCommand = elementCommand;
        this.joinCommand = joinCommand;
        this.mapCommand = mapCommand;
        this.arenaIdCommandCompletionHandler = arenaIdCommandCompletionHandler;
        this.mapNameCommandCompletionHandler = mapNameCommandCompletionHandler;
        this.arenaModeAbsenceCondition = arenaModeAbsenceCondition;
        this.existentArenaIdCondition = existentArenaIdCondition;
        this.nonexistentArenaIdCondition = nonexistentArenaIdCondition;
        this.existentMapNameCondition = existentMapNameCondition;
        this.nonexistentMapNameCondition = nonexistentMapNameCondition;
        this.mapSelectedCondition = mapSelectedCondition;
        this.translator = translator;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        // Arena commands
        String createArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATE_ARENA.getPath()).getText();
        String mapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_MAP.getPath()).getText();
        String removeArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVE_ARENA.getPath()).getText();

        arenaCommand.addCommandInfo(new CommandInfo(createArenaCommandDescription, CREATE_ARENA_COMMAND_USAGE, CREATE_ARENA_COMMAND_SUGGESTION, CREATE_ARENA_COMMAND_PERMISSIONS));
        arenaCommand.addCommandInfo(new CommandInfo(mapCommandDescription, MAP_COMMAND_USAGE, MAP_COMMAND_SUGGESTION, MAP_COMMAND_PERMISSIONS));
        arenaCommand.addCommandInfo(new CommandInfo(removeArenaCommandDescription, REMOVE_ARENA_COMMAND_USAGE, REMOVE_ARENA_COMMAND_SUGGESTION, REMOVE_ARENA_COMMAND_PERMISSIONS));

        // Map commands
        String createMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATE_MAP.getPath()).getText();
        String removeMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVE_MAP.getPath()).getText();
        String selectMapCommandDescription = translator.translate(TranslationKey.DESCRIPTION_MAP_SELECT.getPath()).getText();

        mapCommand.addCommandInfo(new CommandInfo(createMapCommandDescription, CREATE_MAP_COMMAND_USAGE, CREATE_MAP_COMMAND_SUGGESTION, CREATE_MAP_COMMAND_PERMISSIONS));
        mapCommand.addCommandInfo(new CommandInfo(removeMapCommandDescription, REMOVE_MAP_COMMAND_USAGE, REMOVE_MAP_COMMAND_SUGGESTION, REMOVE_MAP_COMMAND_PERMISSIONS));
        mapCommand.addCommandInfo(new CommandInfo(selectMapCommandDescription, SELECT_MAP_COMMAND_USAGE, SELECT_MAP_COMMAND_SUGGESTION, SELECT_MAP_COMMAND_PERMISSIONS));

        // Element commands
        String addElementCommandDescription = translator.translate(TranslationKey.DESCRIPTION_ELEMENT_ADD.getPath()).getText();

        elementCommand.addCommandInfo(new CommandInfo(addElementCommandDescription, ADD_ELEMENT_COMMAND_USAGE, ADD_ELEMENT_COMMAND_SUGGESTION, ADD_ELEMENT_COMMAND_PERMISSIONS));

        commandManager.registerCommand(arenaCommand);
        commandManager.registerCommand(elementCommand);
        commandManager.registerCommand(joinCommand);
        commandManager.registerCommand(mapCommand);

        var commandCompletions = commandManager.getCommandCompletions();
        commandCompletions.registerCompletion("arena-id", arenaIdCommandCompletionHandler);
        commandCompletions.registerCompletion("map-name", mapNameCommandCompletionHandler);

        var commandConditions = commandManager.getCommandConditions();
        commandConditions.addCondition(Integer.class, "existent-arena-id", existentArenaIdCondition);
        commandConditions.addCondition(Integer.class, "nonexistent-arena-id", nonexistentArenaIdCondition);
        commandConditions.addCondition(String.class, "existent-map-name", existentMapNameCondition);
        commandConditions.addCondition(String.class, "nonexistent-map-name", nonexistentMapNameCondition);
        commandConditions.addCondition("arena-absence", arenaModeAbsenceCondition);
        commandConditions.addCondition("map-selected", mapSelectedCondition);
    }
}
