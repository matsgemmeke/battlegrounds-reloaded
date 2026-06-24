package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.arena.ArenaCommand;
import nl.matsgemmeke.battlegrounds.command.arena.CreateArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.arena.RemoveArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.FreeplayModePresenceCondition;
import nl.matsgemmeke.battlegrounds.command.condition.NonexistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.command.tool.ToolsCommand;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

public class CommandBootstrapper {

    private static final String ARENA_COMMAND_USAGE = "/bg arena";
    private static final String ARENA_COMMAND_SUGGESTION = "/bg arena";
    private static final String[] ARENA_COMMAND_PERMISSIONS = new String[] { "bg.arena" };

    private static final String GIVE_WEAPON_COMMAND_USAGE = "/bg giveweapon <weapon>";
    private static final String GIVE_WEAPON_COMMAND_SUGGESTION = "/bg giveweapon ";
    private static final String[] GIVE_WEAPON_COMMAND_PERMISSIONS = new String[] { "bg.giveweapon" };

    private static final String RELOAD_COMMAND_USAGE = "/bg reload";
    private static final String RELOAD_COMMAND_SUGGESTION = "/bg reload";
    private static final String[] RELOAD_COMMAND_PERMISSIONS = new String[] { "battlegrounds.reload" };

    private static final String SET_MAIN_LOBBY_COMMAND_USAGE = "/bg setmainlobby";
    private static final String SET_MAIN_LOBBY_COMMAND_SUGGESTION = "/bg setmainlobby";
    private static final String[] SET_MAIN_LOBBY_COMMAND_PERMISSIONS = new String[] { "battlegrounds.setmainlobby" };

    private final PaperCommandManager commandManager;
    private final Translator translator;

    private final BattlegroundsCommand bgCommand;
    private final ArenaCommand arenaCommand;
    private final ToolsCommand toolsCommand;

    private final ExistentArenaIdCondition existentArenaIdCondition;
    private final NonexistentArenaIdCondition nonexistentArenaIdCondition;
    private final FreeplayModePresenceCondition freeplayModePresenceCondition;

    @Inject
    public CommandBootstrapper(
            PaperCommandManager commandManager,
            Translator translator,
            BattlegroundsCommand bgCommand,
            ArenaCommand arenaCommand,
            ToolsCommand toolsCommand,
            ExistentArenaIdCondition existentArenaIdCondition,
            NonexistentArenaIdCondition nonexistentArenaIdCondition,
            FreeplayModePresenceCondition freeplayModePresenceCondition
    ) {
        this.commandManager = commandManager;
        this.translator = translator;
        this.bgCommand = bgCommand;
        this.arenaCommand = arenaCommand;
        this.toolsCommand = toolsCommand;
        this.existentArenaIdCondition = existentArenaIdCondition;
        this.nonexistentArenaIdCondition = nonexistentArenaIdCondition;
        this.freeplayModePresenceCondition = freeplayModePresenceCondition;
    }

    public void initialize() {
        this.registerBattlegroundsCommand(commandManager);
        this.registerArenaCommand(commandManager);
        this.registerConditions(commandManager);
    }

    private void registerBattlegroundsCommand(PaperCommandManager commandManager) {
        String arenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_ARENA.getPath()).getText();
        String giveWeaponCommandDescription = translator.translate(TranslationKey.DESCRIPTION_GIVE_WEAPON.getPath()).getText();
        String reloadCommandDescription = translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath()).getText();
        String setMainLobbyCommandDescription = translator.translate(TranslationKey.DESCRIPTION_SET_MAIN_LOBBY.getPath()).getText();

        CommandInfo arenaCommandInfo = new CommandInfo(arenaCommandDescription, ARENA_COMMAND_USAGE, ARENA_COMMAND_SUGGESTION, ARENA_COMMAND_PERMISSIONS);
        CommandInfo giveWeaponCommandInfo = new CommandInfo(giveWeaponCommandDescription, GIVE_WEAPON_COMMAND_USAGE, GIVE_WEAPON_COMMAND_SUGGESTION, GIVE_WEAPON_COMMAND_PERMISSIONS);
        CommandInfo reloadCommandInfo = new CommandInfo(reloadCommandDescription, RELOAD_COMMAND_USAGE, RELOAD_COMMAND_SUGGESTION, RELOAD_COMMAND_PERMISSIONS);
        CommandInfo setMainLobbyCommandInfo = new CommandInfo(setMainLobbyCommandDescription, SET_MAIN_LOBBY_COMMAND_USAGE, SET_MAIN_LOBBY_COMMAND_SUGGESTION, SET_MAIN_LOBBY_COMMAND_PERMISSIONS);

        bgCommand.addCommandInfo(arenaCommandInfo);
        bgCommand.addCommandInfo(giveWeaponCommandInfo);
        bgCommand.addCommandInfo(reloadCommandInfo);
        bgCommand.addCommandInfo(setMainLobbyCommandInfo);

        commandManager.registerCommand(bgCommand);
        commandManager.registerCommand(toolsCommand);
    }

    private void registerArenaCommand(PaperCommandManager commandManager) {
        String createArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATE_ARENA.getPath()).getText();
        String removeArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVE_ARENA.getPath()).getText();

        CommandInfo createArenaCommandInfo = new CommandInfo(createArenaCommandDescription, CreateArenaCommandExecutor.USAGE, CreateArenaCommandExecutor.SUGGESTION, CreateArenaCommandExecutor.PERMISSIONS);
        CommandInfo removeArenaCommandInfo = new CommandInfo(removeArenaCommandDescription, RemoveArenaCommandExecutor.USAGE, RemoveArenaCommandExecutor.SUGGESTION, RemoveArenaCommandExecutor.PERMISSIONS);

        arenaCommand.addCommandInfo(createArenaCommandInfo);
        arenaCommand.addCommandInfo(removeArenaCommandInfo);

        commandManager.registerCommand(arenaCommand);
    }

    private void registerConditions(PaperCommandManager commandManager) {
        var commandConditions = commandManager.getCommandConditions();
        commandConditions.addCondition("freeplay-mode-presence", freeplayModePresenceCondition);
        commandConditions.addCondition(Integer.class, "existent-arena-id", existentArenaIdCondition);
        commandConditions.addCondition(Integer.class, "nonexistent-arena-id", nonexistentArenaIdCondition);
    }
}
