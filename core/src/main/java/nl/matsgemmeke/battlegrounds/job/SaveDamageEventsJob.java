package nl.matsgemmeke.battlegrounds.job;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEventTracker;
import nl.matsgemmeke.battlegrounds.scheduling.TaskRunner;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.util.TextUtil;

import java.util.List;
import java.util.logging.Logger;

public class SaveDamageEventsJob implements Job {

    private final DamageEventTracker damageEventTracker;
    private final Logger logger;
    private final TaskRunner taskRunner;

    @Inject
    public SaveDamageEventsJob(DamageEventTracker damageEventTracker, Logger logger, TaskRunner taskRunner) {
        this.damageEventTracker = damageEventTracker;
        this.logger = logger;
        this.taskRunner = taskRunner;
    }

    @Override
    public void run() {
        taskRunner.runTaskAsynchronously(() -> {
            List<DamageEvent> savedEvents = damageEventTracker.saveAll();

            if (!savedEvents.isEmpty()) {
                String eventNoun = TextUtil.pluralize(savedEvents.size(), "event", "events");

                logger.info("[Battlegrounds] Saved %s damage %s to the database".formatted(savedEvents.size(), eventNoun));
            }
        });
    }
}
