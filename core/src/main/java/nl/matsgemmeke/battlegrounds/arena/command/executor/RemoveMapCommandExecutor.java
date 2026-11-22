package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.entity.Player;

import java.util.HashSet;
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

        Replacement[] replacements = {
                Replacement.parsed("arena-id", String.valueOf(arenaId)),
                Replacement.parsed("map-name", mapName)
        };

        // Extra check, the command should have already validated the arena id
        if (arena == null) {
            logger.severe("Arena %s is null in remove map command despite prior validation".formatted(arenaId));
            translator.translateMessage(TranslationKey.MAP_REMOVAL_FAILED.getPath(), replacements).ifPresent(message -> message.send(player));
            return;
        }

        ArenaMap map = arena.getMap(mapName).orElse(null);

        // Extra check, the command should have already validated the map name
        if (map == null) {
            logger.severe("Map %s in arena %s is null in remove map command despite prior validation".formatted(mapName, arenaId));
            translator.translateMessage(TranslationKey.MAP_REMOVAL_FAILED.getPath(), replacements).ifPresent(message -> message.send(player));
            return;
        }

        if (!confirmList.contains(player)) {
            confirmList.add(player);

            translator.translateMessage(TranslationKey.MAP_CONFIRM_REMOVAL.getPath(), replacements).ifPresent(message -> message.send(player));

            Schedule schedule = scheduler.createSingleRunSchedule(CONFIRM_LIST_COOLDOWN);
            schedule.addTask(() -> confirmList.remove(player));
            schedule.start();
            return;
        }

        confirmList.remove(player);

        arena.removeMap(map);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(arenaId);
        setupConfiguration.removeMap(mapName);

        translator.translateMessage(TranslationKey.MAP_REMOVED.getPath(), replacements).ifPresent(message -> message.send(player));
    }
}
