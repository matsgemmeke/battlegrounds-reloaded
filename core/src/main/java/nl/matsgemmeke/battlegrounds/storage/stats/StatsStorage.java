package nl.matsgemmeke.battlegrounds.storage.stats;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventRepository;

public class StatsStorage {

    private final DamageEventRepository damageEventRepository;

    @Inject
    public StatsStorage(DamageEventRepository damageEventRepository) {
        this.damageEventRepository = damageEventRepository;
    }

    public void saveDamageEvent(DamageEvent damageEvent) {
        damageEventRepository.save(damageEvent);
    }
}
