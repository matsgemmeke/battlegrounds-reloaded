package nl.matsgemmeke.battlegrounds.location.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfiguration;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import nl.matsgemmeke.battlegrounds.util.world.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetMainLobbyCommandExecutor {

    private final LocationConfiguration locationConfiguration;
    private final LocationMapper locationMapper;
    private final Translator translator;

    @Inject
    public SetMainLobbyCommandExecutor(LocationConfiguration locationConfiguration, LocationMapper locationMapper, Translator translator) {
        this.locationConfiguration = locationConfiguration;
        this.locationMapper = locationMapper;
        this.translator = translator;
    }

    public void execute(Player player) {
        // Get the center location of the block the player is standing on
        Location location = LocationUtils.getCenterLocation(player.getLocation());
        LocationData locationData = locationMapper.toLocationData(location);

        locationConfiguration.setMainLobbyLocation(locationData);

        player.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath()).getText());
    }
}
