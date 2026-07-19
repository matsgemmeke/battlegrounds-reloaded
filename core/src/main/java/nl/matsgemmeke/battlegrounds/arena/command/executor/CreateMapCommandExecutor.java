package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationResolver;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.MapCreationInfo;
import nl.matsgemmeke.battlegrounds.arena.exception.ArenaNotFoundException;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class CreateMapCommandExecutor {

    private final ArenaRegistry arenaRegistry;
    private final ArenaSetupConfigurationResolver arenaSetupConfigurationResolver;
    private final Clock clock;
    private final Translator translator;

    @Inject
    public CreateMapCommandExecutor(ArenaRegistry arenaRegistry, ArenaSetupConfigurationResolver arenaSetupConfigurationResolver, Clock clock, Translator translator) {
        this.arenaRegistry = arenaRegistry;
        this.arenaSetupConfigurationResolver = arenaSetupConfigurationResolver;
        this.clock = clock;
        this.translator = translator;
    }

    public void execute(Player player, int arenaId, String mapName) {
        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null) {
            throw new ArenaNotFoundException("Received a supposedly validated arena id %s, but the arena instance is not present".formatted(arenaId));
        }

        ArenaMap map = new ArenaMap(mapName);

        arena.addMap(map);

        Instant createdAt = Instant.now(clock);
        UUID createdBy = player.getUniqueId();
        MapCreationInfo mapCreationInfo = new MapCreationInfo(mapName, createdAt, createdBy);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationResolver.resolve(arenaId);
        setupConfiguration.createMap(mapCreationInfo);
        setupConfiguration.save();

        Map<String, Object> values = Map.of("bg_arena", arenaId, "bg_map", mapName);

        player.sendMessage(translator.translate(TranslationKey.MAP_CREATED.getPath()).replace(values));
    }
}
