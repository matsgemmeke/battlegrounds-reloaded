package nl.matsgemmeke.battlegrounds.arena.command.executor.element;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.spawn.CreateSpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.element.ElementRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.element.SpawnPoint;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Math;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class AddSpawnPointCommandExecutor {

    private static final double BLOCK_CENTER_OFFSET = 0.5;

    private final ArenaMapSelector mapSelector;
    private final ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    private final Logger logger;
    private final Translator translator;

    @Inject
    public AddSpawnPointCommandExecutor(
            ArenaMapSelector mapSelector,
            ArenaSetupConfigurationProvider arenaSetupConfigurationProvider,
            @Named("Battlegrounds") Logger logger,
            Translator translator
    ) {
        this.mapSelector = mapSelector;
        this.arenaSetupConfigurationProvider = arenaSetupConfigurationProvider;
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
        ElementRegistry elementRegistry = map.getElementRegistry();

        int elementId = elementRegistry.generateNextId();
        Location location = this.getCenterLocation(player.getLocation());
        SpawnPoint spawnPoint = new SpawnPoint(elementId, teamId, location);

        elementRegistry.addElement(spawnPoint);

        CreateSpawnPointData data = new CreateSpawnPointData(mapName, elementId, location, teamId);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(arenaId);
        setupConfiguration.createSpawnPoint(data);

        Map<String, Object> values = Map.of(
                "bg_element_id", elementId,
                "bg_map_name", mapName
        );

        player.sendMessage(translator.translate(TranslationKey.SPAWN_POINT_ADDED.getPath()).replace(values));
    }

    private Location getCenterLocation(Location original) {
        World world = original.getWorld();
        double x = Math.floor(original.getX()) + BLOCK_CENTER_OFFSET;
        double y = original.getY();
        double z = Math.floor(original.getZ()) + BLOCK_CENTER_OFFSET;
        return new Location(world, x, y, z);
    }
}
