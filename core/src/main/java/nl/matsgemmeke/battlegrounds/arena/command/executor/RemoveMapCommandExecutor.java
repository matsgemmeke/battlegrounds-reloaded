package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RemoveMapCommandExecutor {

    private static final long CONFIRM_LIST_COOLDOWN = 200L;

    private final ArenaRegistry arenaRegistry;
    private final ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    private final Logger logger;
    private final Scheduler scheduler;
    private final Set<Player> confirmList;
    private final Translator translator;

    @Inject
    public RemoveMapCommandExecutor(
            ArenaRegistry arenaRegistry,
            ArenaSetupConfigurationProvider arenaSetupConfigurationProvider,
            @Named("Battlegrounds") Logger logger,
            Scheduler scheduler,
            Translator translator
    ) {
        this.arenaRegistry = arenaRegistry;
        this.arenaSetupConfigurationProvider = arenaSetupConfigurationProvider;
        this.logger = logger;
        this.scheduler = scheduler;
        this.translator = translator;
        this.confirmList = new HashSet<>();
    }

    public void execute(Player player, int arenaId, String mapName) {
        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);
        Map<String, Object> textTemplateValues = Map.of("bg_arena", arenaId, "bg_map", mapName);

        // Extra check, the command should have already validated the arena id
        if (arena == null) {
            player.sendMessage(translator.translate(TranslationKey.MAP_REMOVAL_FAILED.getPath()).replace(textTemplateValues));
            logger.severe("Arena %s is null in remove map command despite prior validation".formatted(arenaId));
            return;
        }

        ArenaMap map = arena.getMap(mapName).orElse(null);

        // Extra check, the command should have already validated the map name
        if (map == null) {
            player.sendMessage(translator.translate(TranslationKey.MAP_REMOVAL_FAILED.getPath()).replace(textTemplateValues));
            logger.severe("Map %s in arena %s is null in remove map command despite prior validation".formatted(mapName, arenaId));
            return;
        }

        if (!confirmList.contains(player)) {
            confirmList.add(player);

            player.sendMessage(translator.translate(TranslationKey.MAP_CONFIRM_REMOVAL.getPath()).replace(textTemplateValues));

            Schedule schedule = scheduler.createSingleRunSchedule(CONFIRM_LIST_COOLDOWN);
            schedule.addTask(() -> confirmList.remove(player));
            schedule.start();
            return;
        }

        confirmList.remove(player);

        arena.removeMap(map);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(arenaId);
        setupConfiguration.removeMap(mapName);

        player.sendMessage(translator.translate(TranslationKey.MAP_REMOVED.getPath()).replace(textTemplateValues));
    }
}
