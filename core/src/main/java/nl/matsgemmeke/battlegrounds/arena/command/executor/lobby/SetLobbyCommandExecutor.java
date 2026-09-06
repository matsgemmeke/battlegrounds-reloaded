package nl.matsgemmeke.battlegrounds.arena.command.executor.lobby;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import nl.matsgemmeke.battlegrounds.util.world.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Logger;

public class SetLobbyCommandExecutor {

    private final ArenaRegistry arenaRegistry;
    private final ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    private final LocationMapper locationMapper;
    private final Logger logger;
    private final Translator translator;

    @Inject
    public SetLobbyCommandExecutor(
            ArenaRegistry arenaRegistry,
            ArenaSetupConfigurationProvider arenaSetupConfigurationProvider,
            LocationMapper locationMapper,
            @Named("Battlegrounds") Logger logger,
            Translator translator
    ) {
        this.arenaRegistry = arenaRegistry;
        this.arenaSetupConfigurationProvider = arenaSetupConfigurationProvider;
        this.locationMapper = locationMapper;
        this.logger = logger;
        this.translator = translator;
    }

    public void execute(Player player, int arenaId) {
        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null) {
            logger.warning("Player %s attempted to set the lobby for arena %s, however no arena was found for this validated arena id".formatted(player.getName(), arenaId));
            player.sendMessage(translator.translate(TranslationKey.SET_LOBBY_FAILED.getPath()).getText());
            return;
        }

        Location lobbyLocation = LocationUtils.getCenterLocation(player.getLocation());
        LocationData lobbyLocationData = locationMapper.toLocationData(lobbyLocation);

        arena.setLobbyLocation(lobbyLocation);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(arenaId);
        setupConfiguration.setLobby(lobbyLocationData);

        Map<String, Object> values = Map.of("bg_arena_id", arenaId);

        player.sendMessage(translator.translate(TranslationKey.SET_LOBBY_SUCCESSFUL.getPath()).replace(values));
    }
}
