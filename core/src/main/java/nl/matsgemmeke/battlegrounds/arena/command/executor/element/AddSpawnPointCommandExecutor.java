package nl.matsgemmeke.battlegrounds.arena.command.executor.element;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.spawn.CreateSpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.element.SpawnPoint;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import nl.matsgemmeke.battlegrounds.util.world.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class AddSpawnPointCommandExecutor {

    private final ArenaMapSelector mapSelector;
    private final ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    private final LocationMapper locationMapper;
    private final Logger logger;
    private final Translator translator;

    @Inject
    public AddSpawnPointCommandExecutor(
            ArenaMapSelector mapSelector,
            ArenaSetupConfigurationProvider arenaSetupConfigurationProvider,
            LocationMapper locationMapper,
            @Named("Battlegrounds") Logger logger,
            Translator translator
    ) {
        this.mapSelector = mapSelector;
        this.arenaSetupConfigurationProvider = arenaSetupConfigurationProvider;
        this.locationMapper = locationMapper;
        this.logger = logger;
        this.translator = translator;
    }

    public void execute(Player player, int teamId) {
        UUID playerId = player.getUniqueId();
        ArenaMapSelection selection = mapSelector.getSelection(playerId).orElse(null);

        // Extra check, the command should have already validated that the player has selected a map
        if (selection == null) {
            player.sendMessage(translator.translate(TranslationKey.GENERIC_ERROR.getPath()).getText());
            logger.severe("Player %s has no map selection despite prior validation".formatted(player.getName()));
            return;
        }

        Arena arena = selection.arena();
        ArenaMap map = selection.map();

        int arenaId = arena.getId();
        String mapName = map.getName();

        int elementId = map.generateNextElementId();
        Location location = LocationUtils.getCenterLocation(player.getLocation());
        LocationData locationData = locationMapper.toLocationData(location);
        SpawnPoint spawnPoint = new SpawnPoint(elementId, teamId, location);

        map.addElement(spawnPoint);

        CreateSpawnPointData data = new CreateSpawnPointData(mapName, elementId, locationData, teamId);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(arenaId);
        setupConfiguration.createSpawnPoint(data);

        Map<String, Object> values = Map.of(
                "bg_element_id", elementId,
                "bg_map_name", mapName
        );

        player.sendMessage(translator.translate(TranslationKey.SPAWN_POINT_ADDED.getPath()).replace(values));
    }
}
