package nl.matsgemmeke.battlegrounds.storage.stats.damage.sqlite;

import nl.matsgemmeke.battlegrounds.storage.DatabaseConfiguration;
import nl.matsgemmeke.battlegrounds.storage.StorageSetupException;
import org.h2.jdbc.JdbcConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqliteDamageEventRepositoryProviderTest {

    private static final String CONNECTION_URL = "jdbc:h2:mem:test";

    @Mock
    private DatabaseConfiguration databaseConfig;
    @InjectMocks
    private SqliteDamageEventRepositoryProvider provider;

    @BeforeEach
    void setUp() {
        when(databaseConfig.getSqliteConnectionUrl()).thenReturn(CONNECTION_URL);
    }

    @Test
    @DisplayName("get throws StorageSetupException when failing to set up database connection")
    void get_databaseConnectionError() {
        try (MockedConstruction<JdbcConnection> jdbcConnectionConstructor = mockConstructionWithAnswer(JdbcConnection.class, invocation -> {
            throw new SQLException("error");
        })) {
            assertThatThrownBy(provider::get).isInstanceOf(StorageSetupException.class).hasMessage("An error occurred while setting up the damage event repository");
            assertThat(jdbcConnectionConstructor.constructed()).hasSize(1);
        }
    }

    @Test
    @DisplayName("get returns instance with injected dao instance")
    void getReturnsSqliteStorageWithInjectedDaoInstances() {
        SqliteDamageEventRepository damageEventRepository = provider.get();

        assertThat(damageEventRepository).isNotNull();
    }
}
