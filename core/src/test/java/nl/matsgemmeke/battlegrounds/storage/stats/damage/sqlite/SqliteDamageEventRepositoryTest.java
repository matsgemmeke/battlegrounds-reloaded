package nl.matsgemmeke.battlegrounds.storage.stats.damage.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SqliteDamageEventRepositoryTest {

    private static final UUID DAMAGER_ID = UUID.randomUUID();
    private static final UUID VICTIM_ID = UUID.randomUUID();
    private static final String ITEM = "MP5";
    private static final double DAMAGE_AMOUNT = 10.0;
    private static final String HITBOX = "BODY";
    private static final double DISTANCE = 20.0;
    private static final boolean KILL = true;
    private static final boolean FRIENDLY_FIRE = false;
    private static final Instant TIMESTAMP = Instant.parse("2026-05-14T13:00:00.00Z");

    private Dao<DamageEventEntity, Integer> damageEventDao;
    private SqliteDamageEventRepository damageEventRepository;

    @BeforeEach
    void setUp() throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:h2:mem:test");
        TableUtils.dropTable(connectionSource, DamageEventEntity.class, true);
        TableUtils.createTable(connectionSource, DamageEventEntity.class);

        damageEventDao = DaoManager.createDao(connectionSource, DamageEventEntity.class);
        damageEventRepository = new SqliteDamageEventRepository(damageEventDao);
    }

    @Test
    @DisplayName("save saves data from given damage event to dao")
    void save_successful() throws SQLException {
        damageEventRepository.save(this.createDamageEvent());

        List<DamageEventEntity> damageEventEntities = damageEventDao.queryForAll();

        assertThat(damageEventEntities).satisfiesExactly(damageEventEntity -> {
            assertThat(damageEventEntity.getDamagerId()).isEqualTo(DAMAGER_ID.toString());
            assertThat(damageEventEntity.getVictimId()).isEqualTo(VICTIM_ID.toString());
            assertThat(damageEventEntity.getItem()).isEqualTo(ITEM);
            assertThat(damageEventEntity.getDamageAmount()).isEqualTo(DAMAGE_AMOUNT);
            assertThat(damageEventEntity.getHitbox()).isEqualTo(HITBOX);
            assertThat(damageEventEntity.getDistance()).isEqualTo(DISTANCE);
            assertThat(damageEventEntity.isKill()).isEqualTo(KILL);
            assertThat(damageEventEntity.isFriendlyFire()).isEqualTo(FRIENDLY_FIRE);
            assertThat(damageEventEntity.getTimestamp()).isEqualTo("2026-05-14T13:00:00Z");
        });
    }

    @Test
    @DisplayName("save throws DamageEventStorageException when failing to save data")
    void save_errorOnSave() throws SQLException {
        Dao<DamageEventEntity, Integer> damageEventDao = mock(RETURNS_DEEP_STUBS);
        when(damageEventDao.create(any(DamageEventEntity.class))).thenThrow(new SQLException("error"));

        SqliteDamageEventRepository damageEventRepository = new SqliteDamageEventRepository(damageEventDao);

        assertThatThrownBy(() -> damageEventRepository.save(this.createDamageEvent()))
                .isInstanceOf(DamageEventStorageException.class)
                .cause()
                .hasMessage("error");
    }

    private DamageEvent createDamageEvent() {
        return new DamageEvent(DAMAGER_ID, VICTIM_ID, ITEM, DAMAGE_AMOUNT, HITBOX, DISTANCE, KILL, FRIENDLY_FIRE, TIMESTAMP);
    }
}
