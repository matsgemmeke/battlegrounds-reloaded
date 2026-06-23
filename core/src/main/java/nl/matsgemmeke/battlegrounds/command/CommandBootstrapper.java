package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.arena.ArenaCommand;
import nl.matsgemmeke.battlegrounds.command.arena.CreateArenaExecutor;
import nl.matsgemmeke.battlegrounds.command.arena.RemoveArenaExecutor;
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
    private final GiveWeaponCommand giveWeaponCommand;
    private final ReloadCommand reloadCommand;
    private final SetMainLobbyCommand setMainLobbyCommand;

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
            GiveWeaponCommand giveWeaponCommand,
            ReloadCommand reloadCommand,
            SetMainLobbyCommand setMainLobbyCommand,
            ToolsCommand toolsCommand,
            ExistentArenaIdCondition existentArenaIdCondition,
            NonexistentArenaIdCondition nonexistentArenaIdCondition,
            FreeplayModePresenceCondition freeplayModePresenceCondition
    ) {
        this.commandManager = commandManager;
        this.translator = translator;
        this.bgCommand = bgCommand;
        this.arenaCommand = arenaCommand;
        this.giveWeaponCommand = giveWeaponCommand;
        this.reloadCommand = reloadCommand;
        this.setMainLobbyCommand = setMainLobbyCommand;
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

        CommandInfo giveWeaponCommandInfo = new CommandInfo(giveWeaponCommandDescription, GiveWeaponCommand.USAGE, GiveWeaponCommand.SUGGESTION, GiveWeaponCommand.PERMISSIONS);
        CommandInfo reloadCommandInfo = new CommandInfo(reloadCommandDescription, ReloadCommand.USAGE, ReloadCommand.SUGGESTION, ReloadCommand.PERMISSIONS);
        CommandInfo setMainLobbyCommandInfo = new CommandInfo(setMainLobbyCommandDescription, SetMainLobbyCommand.USAGE, SetMainLobbyCommand.SUGGESTION, SetMainLobbyCommand.PERMISSIONS);

        bgCommand.addCommandInfo(giveWeaponCommandInfo);
        bgCommand.addCommandInfo(reloadCommandInfo);
        bgCommand.addCommandInfo(setMainLobbyCommandInfo);

        bgCommand.addSubcommand(giveWeaponCommandInfo, giveWeaponCommand);
        bgCommand.addSubcommand(reloadCommandInfo, reloadCommand);
        bgCommand.addSubcommand(setMainLobbyCommandInfo, setMainLobbyCommand);

        commandManager.registerCommand(bgCommand);
        commandManager.registerCommand(toolsCommand);
    }

    private void registerArenaCommand(PaperCommandManager commandManager) {
        String createArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_CREATEARENA.getPath()).getText();
        String removeArenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_REMOVEARENA.getPath()).getText();

        CommandInfo createArenaCommandInfo = new CommandInfo(createArenaCommandDescription, CreateArenaExecutor.USAGE, CreateArenaExecutor.SUGGESTION, CreateArenaExecutor.PERMISSIONS);
        CommandInfo removeArenaCommandInfo = new CommandInfo(removeArenaCommandDescription, RemoveArenaExecutor.USAGE, RemoveArenaExecutor.SUGGESTION, RemoveArenaExecutor.PERMISSIONS);

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
