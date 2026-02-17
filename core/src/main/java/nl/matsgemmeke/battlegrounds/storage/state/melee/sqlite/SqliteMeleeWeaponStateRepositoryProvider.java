package nl.matsgemmeke.battlegrounds.storage.state.melee.sqlite;

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

public class SqliteMeleeWeaponStateRepositoryProvider implements Provider<SqliteMeleeWeaponStateRepository> {

    private final DatabaseConfiguration databaseConfiguration;

    @Inject
    public SqliteMeleeWeaponStateRepositoryProvider(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    public SqliteMeleeWeaponStateRepository get() {
        String connectionUrl = databaseConfiguration.getSqliteConnectionUrl();

        Dao<MeleeWeapon, Integer> meleeWeaponDao;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionUrl);
            TableUtils.createTableIfNotExists(connectionSource, MeleeWeapon.class);

            meleeWeaponDao = DaoManager.createDao(connectionSource, MeleeWeapon.class);
        } catch (SQLException e) {
            throw new StorageSetupException(e.getMessage());
        }

        return new SqliteMeleeWeaponStateRepository(meleeWeaponDao);
    }
}
