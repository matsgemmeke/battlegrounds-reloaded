package nl.matsgemmeke.battlegrounds.storage.sqlite;

import com.j256.ormlite.dao.Dao;
import nl.matsgemmeke.battlegrounds.storage.entity.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GamePlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SqliteStorageTest {

    private Dao<Gun, Integer> gunDao;
    @Captor
    private ArgumentCaptor<List<Gun>> gunsCaptor;

    @BeforeEach
    public void setUp() {
        gunDao = mock();
    }

    @Test
    public void saveGamePlayerStateSavesDataFromGivenGamePlayerStateToDatabase() throws SQLException {
        UUID playerUuid = UUID.randomUUID();
        String gunId = "TEST_GUN";
        int magazineAmmo = 10;
        int reserveAmmo = 20;
        GunState gunState = new GunState(gunId, magazineAmmo, reserveAmmo);
        GamePlayerState gamePlayerState = new GamePlayerState(playerUuid, List.of(gunState));

        SqliteStorage storage = new SqliteStorage(gunDao);
        storage.saveGamePlayerState(gamePlayerState);

        verify(gunDao).create(gunsCaptor.capture());

        List<Gun> savedGuns = gunsCaptor.getValue();

        assertThat(savedGuns).hasSize(1);
        assertThat(savedGuns.get(0).getPlayerUuid()).isEqualTo(playerUuid.toString());
        assertThat(savedGuns.get(0).getGunId()).isEqualTo(gunId);
        assertThat(savedGuns.get(0).getMagazineAmmo()).isEqualTo(magazineAmmo);
        assertThat(savedGuns.get(0).getReserveAmmo()).isEqualTo(reserveAmmo);
    }

    @Test
    public void saveGamePlayerStateThrowsStateStorageExceptionWhenFailingToSaveData() throws SQLException {
        UUID playerUuid = UUID.randomUUID();
        GamePlayerState gamePlayerState = new GamePlayerState(playerUuid, List.of());

        when(gunDao.create(anyCollection())).thenThrow(new SQLException("error"));

        SqliteStorage storage = new SqliteStorage(gunDao);

        assertThatThrownBy(() -> storage.saveGamePlayerState(gamePlayerState)).isInstanceOf(StateStorageException.class).hasMessage("error");
    }
}
