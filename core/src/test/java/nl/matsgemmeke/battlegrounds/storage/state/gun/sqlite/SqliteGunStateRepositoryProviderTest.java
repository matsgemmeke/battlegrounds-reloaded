package nl.matsgemmeke.battlegrounds.storage.state.gun.sqlite;

import nl.matsgemmeke.battlegrounds.storage.DatabaseConfiguration;
import nl.matsgemmeke.battlegrounds.storage.StorageSetupException;
import org.h2.jdbc.JdbcConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class SqliteGunStateRepositoryProviderTest {

    private static final String CONNECTION_URL = "jdbc:h2:mem:test";

    private DatabaseConfiguration databaseConfig;

    @BeforeEach
    public void setUp() {
        databaseConfig = mock(DatabaseConfiguration.class);
        when(databaseConfig.getSqliteConnectionUrl()).thenReturn(CONNECTION_URL);
    }

    @Test
    public void getThrowsStorageSetupExceptionWhenFailingToSetUpDatabaseConnection() {
        try (MockedConstruction<JdbcConnection> jdbcConnectionConstructor = mockConstructionWithAnswer(JdbcConnection.class, invocation -> {
            throw new SQLException("error");
        })) {
            SqliteGunStateRepositoryProvider provider = new SqliteGunStateRepositoryProvider(databaseConfig);

            assertThatThrownBy(provider::get).isInstanceOf(StorageSetupException.class).hasMessage("error");
            assertThat(jdbcConnectionConstructor.constructed()).hasSize(1);
        }
    }

    @Test
    public void getReturnsSqliteStorageWithInjectedDaoInstances() {
        SqliteGunStateRepositoryProvider provider = new SqliteGunStateRepositoryProvider(databaseConfig);
        SqliteGunStateRepository gunStateRepository = provider.get();

        assertThat(gunStateRepository).isNotNull();
    }
}
