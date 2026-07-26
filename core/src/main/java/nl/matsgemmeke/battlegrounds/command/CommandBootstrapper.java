package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.condition.FreeplayModePresenceCondition;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

import java.util.Set;

public class CommandBootstrapper {

    private static final String ARENA_COMMAND_USAGE = "/bg arena";
    private static final String ARENA_COMMAND_SUGGESTION = "/bg arena";
    private static final String[] ARENA_COMMAND_PERMISSIONS = new String[] { "battlegrounds.arena" };

    private static final String ELEMENT_COMMAND_USAGE = "/bg element";
    private static final String ELEMENT_COMMAND_SUGGESTION = "/bg element";
    private static final String[] ELEMENT_COMMAND_PERMISSIONS = new String[] { "battlegrounds.element" };

    private static final String GIVE_WEAPON_COMMAND_USAGE = "/bg giveweapon <weapon>";
    private static final String GIVE_WEAPON_COMMAND_SUGGESTION = "/bg giveweapon ";
    private static final String[] GIVE_WEAPON_COMMAND_PERMISSIONS = new String[] { "battlegrounds.giveweapon" };

    private static final String RELOAD_COMMAND_USAGE = "/bg reload";
    private static final String RELOAD_COMMAND_SUGGESTION = "/bg reload";
    private static final String[] RELOAD_COMMAND_PERMISSIONS = new String[] { "battlegrounds.reload" };

    private static final String SET_MAIN_LOBBY_COMMAND_USAGE = "/bg setmainlobby";
    private static final String SET_MAIN_LOBBY_COMMAND_SUGGESTION = "/bg setmainlobby";
    private static final String[] SET_MAIN_LOBBY_COMMAND_PERMISSIONS = new String[] { "battlegrounds.setmainlobby" };

    private static final String TOOLS_COMMAND_USAGE = "/bg tools";
    private static final String TOOLS_COMMAND_SUGGESTION = "/bg tools";
    private static final String[] TOOLS_COMMAND_PERMISSIONS = new String[] { "battlegrounds.tools" };

    private final PaperCommandManager commandManager;
    private final Set<CommandExtension> commandExtensions;
    private final Translator translator;

    private final BattlegroundsCommand bgCommand;

    private final FreeplayModePresenceCondition freeplayModePresenceCondition;

    @Inject
    public CommandBootstrapper(
            PaperCommandManager commandManager,
            Set<CommandExtension> commandExtensions,
            Translator translator,
            BattlegroundsCommand bgCommand,
            FreeplayModePresenceCondition freeplayModePresenceCondition
    ) {
        this.commandManager = commandManager;
        this.commandExtensions = commandExtensions;
        this.translator = translator;
        this.bgCommand = bgCommand;
        this.freeplayModePresenceCondition = freeplayModePresenceCondition;
    }

    public void initialize() {
        commandExtensions.forEach(extension -> extension.configure(commandManager));

        this.registerBattlegroundsCommand();
        this.registerConditions();
    }

    private void registerBattlegroundsCommand() {
        String arenaCommandDescription = translator.translate(TranslationKey.DESCRIPTION_ARENA.getPath()).getText();
        String elementCommandDescription = translator.translate(TranslationKey.DESCRIPTION_ELEMENT.getPath()).getText();
        String giveWeaponCommandDescription = translator.translate(TranslationKey.DESCRIPTION_GIVE_WEAPON.getPath()).getText();
        String reloadCommandDescription = translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath()).getText();
        String setMainLobbyCommandDescription = translator.translate(TranslationKey.DESCRIPTION_SET_MAIN_LOBBY.getPath()).getText();
        String toolsCommandDescription = translator.translate(TranslationKey.DESCRIPTION_TOOLS.getPath()).getText();

        CommandInfo arenaCommandInfo = new CommandInfo(arenaCommandDescription, ARENA_COMMAND_USAGE, ARENA_COMMAND_SUGGESTION, ARENA_COMMAND_PERMISSIONS);
        CommandInfo elementCommandInfo = new CommandInfo(elementCommandDescription, ELEMENT_COMMAND_USAGE, ELEMENT_COMMAND_SUGGESTION, ELEMENT_COMMAND_PERMISSIONS);
        CommandInfo giveWeaponCommandInfo = new CommandInfo(giveWeaponCommandDescription, GIVE_WEAPON_COMMAND_USAGE, GIVE_WEAPON_COMMAND_SUGGESTION, GIVE_WEAPON_COMMAND_PERMISSIONS);
        CommandInfo reloadCommandInfo = new CommandInfo(reloadCommandDescription, RELOAD_COMMAND_USAGE, RELOAD_COMMAND_SUGGESTION, RELOAD_COMMAND_PERMISSIONS);
        CommandInfo setMainLobbyCommandInfo = new CommandInfo(setMainLobbyCommandDescription, SET_MAIN_LOBBY_COMMAND_USAGE, SET_MAIN_LOBBY_COMMAND_SUGGESTION, SET_MAIN_LOBBY_COMMAND_PERMISSIONS);
        CommandInfo toolsCommandInfo = new CommandInfo(toolsCommandDescription, TOOLS_COMMAND_USAGE, TOOLS_COMMAND_SUGGESTION, TOOLS_COMMAND_PERMISSIONS);

        bgCommand.addCommandInfo(arenaCommandInfo);
        bgCommand.addCommandInfo(elementCommandInfo);
        bgCommand.addCommandInfo(giveWeaponCommandInfo);
        bgCommand.addCommandInfo(reloadCommandInfo);
        bgCommand.addCommandInfo(setMainLobbyCommandInfo);
        bgCommand.addCommandInfo(toolsCommandInfo);

        commandManager.registerCommand(bgCommand);
    }

    private void registerConditions() {
        var commandConditions = commandManager.getCommandConditions();
        commandConditions.addCondition("freeplay-mode-presence", freeplayModePresenceCondition);
    }
}
