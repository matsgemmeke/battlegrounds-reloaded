package nl.matsgemmeke.battlegrounds.job;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEventTracker;
import nl.matsgemmeke.battlegrounds.scheduling.TaskRunner;
import nl.matsgemmeke.battlegrounds.storage.stats.StatsStorage;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.util.TextUtil;

import java.util.List;
import java.util.logging.Logger;

public class SaveDamageEventsJob implements Job {

    private final DamageEventTracker damageEventTracker;
    private final Logger logger;
    private final StatsStorage statsStorage;
    private final TaskRunner taskRunner;

    @Inject
    public SaveDamageEventsJob(DamageEventTracker damageEventTracker, Logger logger, StatsStorage statsStorage, TaskRunner taskRunner) {
        this.damageEventTracker = damageEventTracker;
        this.logger = logger;
        this.statsStorage = statsStorage;
        this.taskRunner = taskRunner;
    }

    @Override
    public void run() {
        taskRunner.runTaskAsynchronously(() -> {
            List<DamageEvent> trackedDamageEvents = damageEventTracker.getTrackedDamageEvents();

            if (!trackedDamageEvents.isEmpty()) {
                statsStorage.saveDamageEvents(trackedDamageEvents);

                String eventNoun = TextUtil.pluralize(trackedDamageEvents.size(), "event", "events");
                logger.info("[Battlegrounds] Saved %s damage %s to the database".formatted(trackedDamageEvents.size(), eventNoun));
            }
        });
    }
}
