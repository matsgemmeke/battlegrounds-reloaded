package nl.matsgemmeke.battlegrounds.storage.stats.damage;

import java.util.Collection;

public interface DamageEventRepository {

    void save(Collection<DamageEvent> damageEvents);
}
