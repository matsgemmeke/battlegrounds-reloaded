package nl.matsgemmeke.battlegrounds.storage.state.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.entity.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SqlitePlayerStateStorageTest {

    private static final UUID PLAYER_UUID = UUID.randomUUID();
    private static final String GUN_ID = "TEST_GUN";
    private static final int GUN_MAGAZINE_AMMO = 10;
    private static final int GUN_RESERVE_AMMO = 20;
    private static final int GUN_ITEM_SLOT = 5;

    private Dao<Gun, Integer> gunDao;

    @BeforeEach
    public void setUp() throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:h2:mem:test");
        TableUtils.dropTable(connectionSource, Gun.class, true);
        TableUtils.createTable(connectionSource, Gun.class);

        gunDao = DaoManager.createDao(connectionSource, Gun.class);
    }

    @Test
    public void findPlayerStateByPlayerUuidThrowsStateStorageExceptionWhenFailingToReadData() throws SQLException {
        Dao<Gun, Integer> gunDao = mock(RETURNS_DEEP_STUBS);
        when(gunDao.queryBuilder().where().eq("player_uuid", PLAYER_UUID.toString()).prepare()).thenThrow(new SQLException("error"));

        SqlitePlayerStateStorage storage = new SqlitePlayerStateStorage(gunDao);

        assertThatThrownBy(() -> storage.findPlayerStateByPlayerUuid(PLAYER_UUID)).isInstanceOf(StateStorageException.class).hasMessage("error");
    }

    @Test
    public void findPlayerStateByPlayerUuidReturnsGamePlayerStateInstanceWithDataFromDao() throws SQLException {
        Gun gun = new Gun();
        gun.setId(1);
        gun.setPlayerUuid(PLAYER_UUID.toString());
        gun.setGunId(GUN_ID);
        gun.setMagazineAmmo(GUN_MAGAZINE_AMMO);
        gun.setReserveAmmo(GUN_RESERVE_AMMO);
        gun.setItemSlot(GUN_ITEM_SLOT);

        gunDao.create(gun);

        SqlitePlayerStateStorage storage = new SqlitePlayerStateStorage(gunDao);
        PlayerState playerState = storage.findPlayerStateByPlayerUuid(PLAYER_UUID);

        assertThat(playerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(playerState.gunStates()).hasSize(1);
        assertThat(playerState.gunStates().get(0).gunId()).isEqualTo(GUN_ID);
        assertThat(playerState.gunStates().get(0).magazineAmmo()).isEqualTo(GUN_MAGAZINE_AMMO);
        assertThat(playerState.gunStates().get(0).reserveAmmo()).isEqualTo(GUN_RESERVE_AMMO);
        assertThat(playerState.gunStates().get(0).itemSlot()).isEqualTo(GUN_ITEM_SLOT);
    }

    @Test
    public void savePlayerStateSavesDataFromGivenGamePlayerStateToDatabase() throws SQLException {
        GunState gunState = new GunState(GUN_ID, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, GUN_ITEM_SLOT);
        PlayerState playerState = new PlayerState(PLAYER_UUID, List.of(gunState));

        SqlitePlayerStateStorage storage = new SqlitePlayerStateStorage(gunDao);
        storage.savePlayerState(playerState);

        List<Gun> savedGuns = gunDao.queryForAll();

        assertThat(savedGuns).hasSize(1);
        assertThat(savedGuns.get(0).getPlayerUuid()).isEqualTo(PLAYER_UUID.toString());
        assertThat(savedGuns.get(0).getGunId()).isEqualTo(GUN_ID);
        assertThat(savedGuns.get(0).getMagazineAmmo()).isEqualTo(GUN_MAGAZINE_AMMO);
        assertThat(savedGuns.get(0).getReserveAmmo()).isEqualTo(GUN_RESERVE_AMMO);
        assertThat(savedGuns.get(0).getItemSlot()).isEqualTo(GUN_ITEM_SLOT);
    }

    @Test
    public void savePlayerStateThrowsStateStorageExceptionWhenFailingToSaveData() throws SQLException {
        UUID playerUuid = UUID.randomUUID();
        PlayerState playerState = new PlayerState(playerUuid, List.of());

        Dao<Gun, Integer> gunDao = mock();
        when(gunDao.create(anyCollection())).thenThrow(new SQLException("error"));

        SqlitePlayerStateStorage storage = new SqlitePlayerStateStorage(gunDao);

        assertThatThrownBy(() -> storage.savePlayerState(playerState)).isInstanceOf(StateStorageException.class).hasMessage("error");
    }
}
