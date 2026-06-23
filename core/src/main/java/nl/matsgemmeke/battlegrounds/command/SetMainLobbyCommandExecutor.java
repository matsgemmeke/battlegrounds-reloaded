package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.data.DataConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetMainLobbyCommandExecutor {

    public static final String USAGE = "/bg setmainlobby";
    public static final String SUGGESTION = "/bg setmainlobby";
    public static final String[] PERMISSIONS = new String[] { "battlegrounds.setmainlobby" };

    private final DataConfiguration dataConfiguration;
    private final Translator translator;

    @Inject
    public SetMainLobbyCommandExecutor(DataConfiguration dataConfiguration, Translator translator) {
        this.dataConfiguration = dataConfiguration;
        this.translator = translator;
    }

    public void execute(Player player) {
        // Get the center location of the block the player is standing on
        Location location = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

        dataConfiguration.setMainLobbyLocation(location);
        dataConfiguration.save();

        player.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath()).getText());
    }
}
