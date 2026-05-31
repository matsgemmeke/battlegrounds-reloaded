package nl.matsgemmeke.battlegrounds.storage.stats.damage.sqlite;

import com.j256.ormlite.dao.Dao;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventRepository;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventStorageException;

import java.sql.SQLException;
import java.util.Collection;

public class SqliteDamageEventRepository implements DamageEventRepository {

    private final Dao<DamageEventEntity, Integer> damageEventDao;

    public SqliteDamageEventRepository(Dao<DamageEventEntity, Integer> damageEventDao) {
        this.damageEventDao = damageEventDao;
    }

    @Override
    public void save(Collection<DamageEvent> damageEvents) {
        Collection<DamageEventEntity> damageEventEntities = damageEvents.stream()
                .map(this::convertDamageEventToDamageEventEntity)
                .toList();

        try {
            damageEventDao.create(damageEventEntities);
        } catch (SQLException ex) {
            throw new DamageEventStorageException("An error occurred while saving damage event", ex);
        }
    }

    private DamageEventEntity convertDamageEventToDamageEventEntity(DamageEvent damageEvent) {
        DamageEventEntity damageEventEntity = new DamageEventEntity();
        damageEventEntity.setGameKey(damageEvent.gameKey().toString());
        damageEventEntity.setDamagerId(damageEvent.damagerId());
        damageEventEntity.setDamagerEntityKey(damageEvent.damagerEntityKey().getValue());
        damageEventEntity.setVictimId(damageEvent.victimId());
        damageEventEntity.setVictimEntityKey(damageEvent.victimEntityType().getValue());
        damageEventEntity.setItem(damageEvent.item());
        damageEventEntity.setDamageAmount(damageEvent.damageAmount());
        damageEventEntity.setDamageType(damageEvent.damageType().name());
        damageEventEntity.setHitbox(damageEvent.hitboxComponentType().name());
        damageEventEntity.setDistance(damageEvent.distance());
        damageEventEntity.setKill(damageEvent.kill());
        damageEventEntity.setFriendlyFire(damageEvent.friendlyFire());
        damageEventEntity.setTimestamp(damageEvent.timestamp().toString());
        return damageEventEntity;
    }
}
