package nl.matsgemmeke.battlegrounds.storage.state.gun.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public class SqliteGunStateRepositoryTest {

    private static final UUID PLAYER_UUID = UUID.randomUUID();
    private static final String GUN_NAME = "Test Gun";
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
    public void deleteByPlayerUuidDeletesDataFromDao() throws SQLException {
        int id = 1;
        Gun gun = this.createGun(id);

        gunDao.create(gun);

        SqliteGunStateRepository repository = new SqliteGunStateRepository(gunDao);
        repository.deleteByPlayerUuid(PLAYER_UUID);

        List<Gun> savedGuns = gunDao.queryForAll();

        assertThat(savedGuns).isEmpty();
    }

    @Test
    public void deleteByPlayerUuidThrowsPlayerStateStorageExceptionWhenFailingToDeleteData() throws SQLException {
        Dao<Gun, Integer> gunDao = mock(RETURNS_DEEP_STUBS);
        when(gunDao.queryBuilder().where().eq("player_uuid", PLAYER_UUID.toString()).prepare()).thenThrow(new SQLException("error"));

        SqliteGunStateRepository repository = new SqliteGunStateRepository(gunDao);

        assertThatThrownBy(() -> repository.deleteByPlayerUuid(PLAYER_UUID))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    @Test
    public void findByPlayerUuidReturnsGunStatesWithDataFromDao() throws SQLException {
        int id = 1;
        Gun gun = this.createGun(id);

        gunDao.create(gun);

        SqliteGunStateRepository repository = new SqliteGunStateRepository(gunDao);
        List<GunState> gunStates = repository.findByPlayerUuid(PLAYER_UUID);

        assertThat(gunStates).hasSize(1);
        assertThat(gunStates.get(0).playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(gunStates.get(0).gunName()).isEqualTo(GUN_NAME);
        assertThat(gunStates.get(0).magazineAmmo()).isEqualTo(GUN_MAGAZINE_AMMO);
        assertThat(gunStates.get(0).reserveAmmo()).isEqualTo(GUN_RESERVE_AMMO);
        assertThat(gunStates.get(0).itemSlot()).isEqualTo(GUN_ITEM_SLOT);
    }

    @Test
    public void findByPlayerUuidThrowsPlayerStateStorageExceptionWhenFailingToReadData() throws SQLException {
        Dao<Gun, Integer> gunDao = mock(RETURNS_DEEP_STUBS);
        when(gunDao.queryBuilder().where().eq("player_uuid", PLAYER_UUID.toString()).prepare()).thenThrow(new SQLException("error"));

        SqliteGunStateRepository repository = new SqliteGunStateRepository(gunDao);

        assertThatThrownBy(() -> repository.findByPlayerUuid(PLAYER_UUID))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    @Test
    public void saveSavesDataFromGivenGunStatesToDao() throws SQLException {
        GunState gunState = new GunState(PLAYER_UUID, GUN_NAME, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, GUN_ITEM_SLOT);
        List<GunState> gunStates = List.of(gunState);

        SqliteGunStateRepository repository = new SqliteGunStateRepository(gunDao);
        repository.save(gunStates);

        List<Gun> savedGuns = gunDao.queryForAll();

        assertThat(savedGuns).hasSize(1);
        assertThat(savedGuns.get(0).getPlayerUuid()).isEqualTo(PLAYER_UUID.toString());
        assertThat(savedGuns.get(0).getGunName()).isEqualTo(GUN_NAME);
        assertThat(savedGuns.get(0).getMagazineAmmo()).isEqualTo(GUN_MAGAZINE_AMMO);
        assertThat(savedGuns.get(0).getReserveAmmo()).isEqualTo(GUN_RESERVE_AMMO);
        assertThat(savedGuns.get(0).getItemSlot()).isEqualTo(GUN_ITEM_SLOT);
    }

    @Test
    public void saveThrowsStateStorageExceptionWhenFailingToSaveData() throws SQLException {
        Dao<Gun, Integer> gunDao = mock(RETURNS_DEEP_STUBS);
        when(gunDao.create(anyCollection())).thenThrow(new SQLException("error"));

        SqliteGunStateRepository repository = new SqliteGunStateRepository(gunDao);

        assertThatThrownBy(() -> repository.save(List.of()))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    private Gun createGun(int id) {
        Gun gun = new Gun();
        gun.setId(id);
        gun.setPlayerUuid(PLAYER_UUID.toString());
        gun.setGunName(GUN_NAME);
        gun.setMagazineAmmo(GUN_MAGAZINE_AMMO);
        gun.setReserveAmmo(GUN_RESERVE_AMMO);
        gun.setItemSlot(GUN_ITEM_SLOT);
        return gun;
    }
}
