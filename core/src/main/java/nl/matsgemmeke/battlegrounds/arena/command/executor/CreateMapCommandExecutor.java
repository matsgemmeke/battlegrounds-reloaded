package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSetupConfigurationFactory;
import nl.matsgemmeke.battlegrounds.arena.exception.ArenaNotFoundException;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class CreateMapCommandExecutor {

    private final ArenaRegistry arenaRegistry;
    private final ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    private final Translator translator;

    @Inject
    public CreateMapCommandExecutor(ArenaRegistry arenaRegistry, ArenaSetupConfigurationFactory arenaSetupConfigurationFactory, Translator translator) {
        this.arenaRegistry = arenaRegistry;
        this.arenaSetupConfigurationFactory = arenaSetupConfigurationFactory;
        this.translator = translator;
    }

    public void execute(CommandSender sender, int arenaId, String mapName) {
        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null) {
            throw new ArenaNotFoundException("Received a supposedly validated arena id %s, but the arena instance is not present".formatted(arenaId));
        }

        ArenaMap map = new ArenaMap(mapName);

        arena.addMap(map);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationFactory.create(arenaId);
        setupConfiguration.createMap(mapName);
        setupConfiguration.save();

        Map<String, Object> values = Map.of("bg_arena", arenaId, "bg_map", mapName);

        sender.sendMessage(translator.translate(TranslationKey.MAP_CREATED.getPath()).replace(values));
    }
}
