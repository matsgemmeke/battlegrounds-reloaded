package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DamageEventTracker {

    private final Queue<DamageEvent> pending;

    public DamageEventTracker() {
        this.pending = new ConcurrentLinkedQueue<>();
    }

    public void add(DamageEvent damageEvent) {
        pending.add(damageEvent);
    }

    public List<DamageEvent> saveAll() {
        List<DamageEvent> batch = new ArrayList<>();

        while (!pending.isEmpty()) {
            batch.add(pending.poll());
        }

        return batch;
    }
}
