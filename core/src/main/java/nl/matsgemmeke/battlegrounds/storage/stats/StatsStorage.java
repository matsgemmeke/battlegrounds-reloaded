package nl.matsgemmeke.battlegrounds.storage.stats;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventRepository;

import java.util.Collection;

public class StatsStorage {

    private final DamageEventRepository damageEventRepository;

    @Inject
    public StatsStorage(DamageEventRepository damageEventRepository) {
        this.damageEventRepository = damageEventRepository;
    }

    public void saveDamageEvents(Collection<DamageEvent> damageEvents) {
        damageEventRepository.save(damageEvents);
    }
}
