package nl.matsgemmeke.battlegrounds.storage.stats.damage.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.fixture.DamageEventFixture;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SqliteDamageEventRepositoryTest {

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
        DamageEvent damageEvent = DamageEventFixture.createDefault();

        damageEventRepository.save(List.of(damageEvent));

        List<DamageEventEntity> damageEventEntities = damageEventDao.queryForAll();

        assertThat(damageEventEntities).satisfiesExactly(damageEventEntity -> {
            assertThat(damageEventEntity.getGameKey()).isEqualTo("FREEPLAY");
            assertThat(damageEventEntity.getSourceId()).hasToString("2c11afe2-48f0-4399-9a04-195bb8ac640e");
            assertThat(damageEventEntity.getSourceEntityKey()).isEqualTo("minecraft:player");
            assertThat(damageEventEntity.getTargetId()).hasToString("606c4672-cf52-4913-85e5-984225ceaed1");
            assertThat(damageEventEntity.getTargetEntityKey()).isEqualTo("minecraft:zombie");
            assertThat(damageEventEntity.getItem()).isEqualTo("MP5");
            assertThat(damageEventEntity.getDamageAmount()).isEqualTo(20.0);
            assertThat(damageEventEntity.getDamageType()).isEqualTo("BULLET_DAMAGE");
            assertThat(damageEventEntity.getHitbox()).isEqualTo("TORSO");
            assertThat(damageEventEntity.getDistance()).isEqualTo(30.0);
            assertThat(damageEventEntity.isKill()).isTrue();
            assertThat(damageEventEntity.isFriendlyFire()).isFalse();
            assertThat(damageEventEntity.getTimestamp()).isEqualTo("2026-05-25T18:00:00Z");
        });
    }

    @Test
    @DisplayName("save throws DamageEventStorageException when failing to save data")
    void save_errorOnSave() throws SQLException {
        DamageEvent damageEvent = DamageEventFixture.createDefault();

        Dao<DamageEventEntity, Integer> damageEventDao = mock(RETURNS_DEEP_STUBS);
        when(damageEventDao.create(anyCollection())).thenThrow(new SQLException("error"));

        SqliteDamageEventRepository damageEventRepository = new SqliteDamageEventRepository(damageEventDao);

        assertThatThrownBy(() -> damageEventRepository.save(List.of(damageEvent)))
                .isInstanceOf(DamageEventStorageException.class)
                .cause()
                .hasMessage("error");
    }
}
