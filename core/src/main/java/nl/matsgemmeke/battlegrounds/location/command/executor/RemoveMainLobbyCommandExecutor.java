package nl.matsgemmeke.battlegrounds.location.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfiguration;
import org.bukkit.entity.Player;

public class RemoveMainLobbyCommandExecutor {

    private final LocationConfiguration locationConfiguration;
    private final Translator translator;

    @Inject
    public RemoveMainLobbyCommandExecutor(LocationConfiguration locationConfiguration, Translator translator) {
        this.locationConfiguration = locationConfiguration;
        this.translator = translator;
    }

    public void execute(Player player) {
        locationConfiguration.removeMainLobbyLocation();

        player.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_REMOVE.getPath()).getText());
    }
}
