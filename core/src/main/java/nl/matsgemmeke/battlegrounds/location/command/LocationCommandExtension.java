package nl.matsgemmeke.battlegrounds.location.command;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.command.condition.ExistentMainLobbyCondition;

public class LocationCommandExtension implements CommandExtension {

    private static final String REMOVE_MAIN_LOBBY_COMMAND_USAGE = "/bg mainlobby remove";
    private static final String REMOVE_MAIN_LOBBY_COMMAND_SUGGESTION = "/bg mainlobby remove";
    private static final String[] REMOVE_MAIN_LOBBY_COMMAND_PERMISSIONS = new String[] { "battlegrounds.mainlobby.remove" };

    private static final String SET_MAIN_LOBBY_COMMAND_USAGE = "/bg mainlobby set";
    private static final String SET_MAIN_LOBBY_COMMAND_SUGGESTION = "/bg mainlobby set";
    private static final String[] SET_MAIN_LOBBY_COMMAND_PERMISSIONS = new String[] { "battlegrounds.mainlobby.set" };

    private final MainLobbyCommand mainLobbyCommand;
    private final ExistentMainLobbyCondition existentMainLobbyCondition;
    private final Translator translator;

    @Inject
    public LocationCommandExtension(MainLobbyCommand mainLobbyCommand, ExistentMainLobbyCondition existentMainLobbyCondition, Translator translator) {
        this.mainLobbyCommand = mainLobbyCommand;
        this.existentMainLobbyCondition = existentMainLobbyCondition;
        this.translator = translator;
    }

    @Override
    public void configure(PaperCommandManager commandManager) {
        String removeMainLobbyCommandDescription = translator.translate(TranslationKey.DESCRIPTION_MAIN_LOBBY_REMOVE.getPath()).getText();
        String setMainLobbyCommandDescription = translator.translate(TranslationKey.DESCRIPTION_MAIN_LOBBY_SET.getPath()).getText();

        mainLobbyCommand.addCommandInfo(new CommandInfo(removeMainLobbyCommandDescription, REMOVE_MAIN_LOBBY_COMMAND_USAGE, REMOVE_MAIN_LOBBY_COMMAND_SUGGESTION, REMOVE_MAIN_LOBBY_COMMAND_PERMISSIONS));
        mainLobbyCommand.addCommandInfo(new CommandInfo(setMainLobbyCommandDescription, SET_MAIN_LOBBY_COMMAND_USAGE, SET_MAIN_LOBBY_COMMAND_SUGGESTION, SET_MAIN_LOBBY_COMMAND_PERMISSIONS));

        commandManager.registerCommand(mainLobbyCommand);

        var commandConditions = commandManager.getCommandConditions();
        commandConditions.addCondition("existent-main-lobby", existentMainLobbyCondition);
    }
}
