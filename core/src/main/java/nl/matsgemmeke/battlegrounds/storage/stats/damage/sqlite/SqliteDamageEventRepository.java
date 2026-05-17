package nl.matsgemmeke.battlegrounds.storage.stats.damage.sqlite;

import com.j256.ormlite.dao.Dao;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventRepository;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventStorageException;

import java.sql.SQLException;

public class SqliteDamageEventRepository implements DamageEventRepository {

    private final Dao<DamageEventEntity, Integer> damageEventDao;

    public SqliteDamageEventRepository(Dao<DamageEventEntity, Integer> damageEventDao) {
        this.damageEventDao = damageEventDao;
    }

    @Override
    public void save(DamageEvent damageEvent) {
        DamageEventEntity damageEventEntity = this.convertDamageEventToDamageEventEntity(damageEvent);

        try {
            damageEventDao.create(damageEventEntity);
        } catch (SQLException ex) {
            throw new DamageEventStorageException("An error occurred while saving damage event", ex);
        }
    }

    private DamageEventEntity convertDamageEventToDamageEventEntity(DamageEvent damageEvent) {
        DamageEventEntity damageEventEntity = new DamageEventEntity();
        damageEventEntity.setGameKey(damageEvent.gameKey());
        damageEventEntity.setDamagerId(damageEvent.damagerId().toString());
        damageEventEntity.setVictimId(damageEvent.victimId().toString());
        damageEventEntity.setItem(damageEvent.item());
        damageEventEntity.setDamageAmount(damageEvent.damageAmount());
        damageEventEntity.setHitbox(damageEvent.hitbox());
        damageEventEntity.setDistance(damageEvent.distance());
        damageEventEntity.setKill(damageEvent.kill());
        damageEventEntity.setFriendlyFire(damageEvent.friendlyFire());
        damageEventEntity.setTimestamp(damageEvent.timestamp().toString());
        return damageEventEntity;
    }
}
