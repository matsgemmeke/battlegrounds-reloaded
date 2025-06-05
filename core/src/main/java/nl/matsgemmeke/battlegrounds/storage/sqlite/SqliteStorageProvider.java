package nl.matsgemmeke.battlegrounds.storage.sqlite;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.DatabaseConfiguration;
import nl.matsgemmeke.battlegrounds.storage.StorageSetupException;
import nl.matsgemmeke.battlegrounds.storage.entity.Gun;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class SqliteStorageProvider implements Provider<SqliteStorage> {

    @NotNull
    private final DatabaseConfiguration databaseConfiguration;

    @Inject
    public SqliteStorageProvider(@NotNull DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    public SqliteStorage get() {
        String connectionUrl = databaseConfiguration.getSqliteConnectionUrl();

        Dao<Gun, Integer> gunDao;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionUrl);
            TableUtils.createTableIfNotExists(connectionSource, Gun.class);

            gunDao = DaoManager.createDao(connectionSource, Gun.class);
        } catch (SQLException e) {
            throw new StorageSetupException(e.getMessage());
        }

        return new SqliteStorage(gunDao);
    }
}
