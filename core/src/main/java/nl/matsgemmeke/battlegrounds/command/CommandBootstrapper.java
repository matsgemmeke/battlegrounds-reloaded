package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.arena.ArenaCommand;
import nl.matsgemmeke.battlegrounds.command.arena.CreateArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.arena.RemoveArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.FreeplayModePresenceCondition;
import nl.matsgemmeke.battlegrounds.command.condition.NonexistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

public class CommandBootstrapper {

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
        String giveWeaponCommandDescription = translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()).getText();
        String reloadCommandDescription = translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath()).getText();
        String setMainLobbyCommandDescription = translator.translate(TranslationKey.DESCRIPTION_SETMAINLOBBY.getPath()).getText();

        CommandInfo giveWeaponCommandInfo = new CommandInfo(giveWeaponCommandDescription, GiveWeaponCommandExecutor.USAGE, GiveWeaponCommandExecutor.SUGGESTION, GiveWeaponCommandExecutor.PERMISSIONS);
        CommandInfo reloadCommandInfo = new CommandInfo(reloadCommandDescription, ReloadCommandExecutor.USAGE, ReloadCommandExecutor.SUGGESTION, ReloadCommandExecutor.PERMISSIONS);
        CommandInfo setMainLobbyCommandInfo = new CommandInfo(setMainLobbyCommandDescription, SetMainLobbyCommandExecutor.USAGE, SetMainLobbyCommandExecutor.SUGGESTION, SetMainLobbyCommandExecutor.PERMISSIONS);

        bgCommand.addCommandInfo(giveWeaponCommandInfo);
        bgCommand.addCommandInfo(reloadCommandInfo);
        bgCommand.addCommandInfo(setMainLobbyCommandInfo);

        commandManager.registerCommand(bgCommand);
        commandManager.registerCommand(toolsCommand);
    }

    private void registerArenaCommand(PaperCommandManager commandManager) {
        String createArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATEARENA.getPath()).getText();
        String removeArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVEARENA.getPath()).getText();

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
