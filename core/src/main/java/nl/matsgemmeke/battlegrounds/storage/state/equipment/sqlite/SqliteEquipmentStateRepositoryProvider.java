package nl.matsgemmeke.battlegrounds.storage.state.equipment.sqlite;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.DatabaseConfiguration;
import nl.matsgemmeke.battlegrounds.storage.StorageSetupException;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class SqliteEquipmentStateRepositoryProvider implements Provider<SqliteEquipmentStateRepository> {

    @NotNull
    private final DatabaseConfiguration databaseConfiguration;

    @Inject
    public SqliteEquipmentStateRepositoryProvider(@NotNull DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    public SqliteEquipmentStateRepository get() {
        String connectionUrl = databaseConfiguration.getSqliteConnectionUrl();

        Dao<Equipment, Integer> equipmentDao;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionUrl);
            TableUtils.createTableIfNotExists(connectionSource, Equipment.class);

            equipmentDao = DaoManager.createDao(connectionSource, Equipment.class);
        } catch (SQLException e) {
            throw new StorageSetupException(e.getMessage());
        }

        return new SqliteEquipmentStateRepository(equipmentDao);
    }
}
