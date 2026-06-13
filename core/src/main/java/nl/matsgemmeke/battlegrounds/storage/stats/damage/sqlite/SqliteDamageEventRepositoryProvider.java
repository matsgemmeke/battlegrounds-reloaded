package nl.matsgemmeke.battlegrounds.storage.stats.damage.sqlite;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.DatabaseConfiguration;
import nl.matsgemmeke.battlegrounds.storage.StorageSetupException;

import java.sql.SQLException;

public class SqliteDamageEventRepositoryProvider implements Provider<SqliteDamageEventRepository> {

    private final DatabaseConfiguration databaseConfiguration;

    @Inject
    public SqliteDamageEventRepositoryProvider(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    public SqliteDamageEventRepository get() {
        String connectionUrl = databaseConfiguration.getSqliteConnectionUrl();

        Dao<DamageEventEntity, Integer> damageEventDao;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionUrl);
            TableUtils.createTableIfNotExists(connectionSource, DamageEventEntity.class);

            damageEventDao = DaoManager.createDao(connectionSource, DamageEventEntity.class);

            return new SqliteDamageEventRepository(damageEventDao);
        } catch (SQLException ex) {
            throw new StorageSetupException("An error occurred while setting up the damage event repository", ex);
        }
    }
}
