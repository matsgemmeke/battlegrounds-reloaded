package nl.matsgemmeke.battlegrounds.storage.state.melee.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

class SqliteMeleeWeaponStateRepositoryTest {

    private static final UUID PLAYER_UUID = UUID.randomUUID();
    private static final String MELEE_WEAPON_NAME = "Test Melee Weapon";
    private static final int MELEE_WEAPON_ITEM_SLOT = 7;

    private Dao<MeleeWeapon, Integer> meleeWeaponDao;

    @BeforeEach
    void setUp() throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:h2:mem:test");
        TableUtils.dropTable(connectionSource, MeleeWeapon.class, true);
        TableUtils.createTable(connectionSource, MeleeWeapon.class);

        meleeWeaponDao = DaoManager.createDao(connectionSource, MeleeWeapon.class);
    }

    @Test
    void deleteByPlayerUuidDeletesDataFromDao() throws SQLException {
        int id = 1;
        MeleeWeapon meleeWeapon = this.createMeleeWeapon(id);

        meleeWeaponDao.create(meleeWeapon);

        SqliteMeleeWeaponStateRepository repository = new SqliteMeleeWeaponStateRepository(meleeWeaponDao);
        repository.deleteByPlayerUuid(PLAYER_UUID);

        List<MeleeWeapon> savedMeleeWeapons = meleeWeaponDao.queryForAll();

        assertThat(savedMeleeWeapons).isEmpty();
    }

    @Test
    void deleteByPlayerUuidThrowsPlayerStateStorageExceptionWhenFailingToDeleteData() throws SQLException {
        Dao<MeleeWeapon, Integer> meleeWeaponDao = mock(RETURNS_DEEP_STUBS);
        when(meleeWeaponDao.queryBuilder().where().eq("player_uuid", PLAYER_UUID.toString()).prepare()).thenThrow(new SQLException("error"));

        SqliteMeleeWeaponStateRepository repository = new SqliteMeleeWeaponStateRepository(meleeWeaponDao);

        assertThatThrownBy(() -> repository.deleteByPlayerUuid(PLAYER_UUID))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    @Test
    void findByPlayerUuidReturnsEquipmentStatesWithDataFromDao() throws SQLException {
        int id = 1;
        MeleeWeapon meleeWeapon = this.createMeleeWeapon(id);

        meleeWeaponDao.create(meleeWeapon);

        SqliteMeleeWeaponStateRepository repository = new SqliteMeleeWeaponStateRepository(meleeWeaponDao);
        List<MeleeWeaponState> meleeWeaponStates = repository.findByPlayerUuid(PLAYER_UUID);

        assertThat(meleeWeaponStates).satisfiesExactly(meleeWeaponState -> {
            assertThat(meleeWeaponState.playerUuid()).isEqualTo(PLAYER_UUID);
            assertThat(meleeWeaponState.meleeWeaponName()).isEqualTo(MELEE_WEAPON_NAME);
            assertThat(meleeWeaponState.itemSlot()).isEqualTo(MELEE_WEAPON_ITEM_SLOT);
        });
    }

    @Test
    void findByPlayerUuidThrowsPlayerStateStorageExceptionWhenFailingToReadData() throws SQLException {
        Dao<MeleeWeapon, Integer> meleeWeaponDao = mock(RETURNS_DEEP_STUBS);
        when(meleeWeaponDao.queryBuilder().where().eq("player_uuid", PLAYER_UUID.toString()).prepare()).thenThrow(new SQLException("error"));

        SqliteMeleeWeaponStateRepository repository = new SqliteMeleeWeaponStateRepository(meleeWeaponDao);

        assertThatThrownBy(() -> repository.findByPlayerUuid(PLAYER_UUID))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    @Test
    void saveSavesDataFromGivenEquipmentStatesToDao() throws SQLException {
        MeleeWeaponState meleeWeaponState = new MeleeWeaponState(PLAYER_UUID, MELEE_WEAPON_NAME, MELEE_WEAPON_ITEM_SLOT);
        List<MeleeWeaponState> meleeWeaponStates = List.of(meleeWeaponState);

        SqliteMeleeWeaponStateRepository repository = new SqliteMeleeWeaponStateRepository(meleeWeaponDao);
        repository.save(meleeWeaponStates);

        List<MeleeWeapon> savedMeleeWeapons = meleeWeaponDao.queryForAll();

        assertThat(savedMeleeWeapons).satisfiesExactly(meleeWeapon -> {
            assertThat(meleeWeapon.getPlayerUuid()).isEqualTo(PLAYER_UUID.toString());
            assertThat(meleeWeapon.getMeleeWeaponName()).isEqualTo(MELEE_WEAPON_NAME);
            assertThat(meleeWeapon.getItemSlot()).isEqualTo(MELEE_WEAPON_ITEM_SLOT);
        });
    }

    @Test
    void saveThrowsStateStorageExceptionWhenFailingToSaveData() throws SQLException {
        Dao<MeleeWeapon, Integer> meleeWeaponDao = mock(RETURNS_DEEP_STUBS);
        when(meleeWeaponDao.create(anyCollection())).thenThrow(new SQLException("error"));

        SqliteMeleeWeaponStateRepository repository = new SqliteMeleeWeaponStateRepository(meleeWeaponDao);

        assertThatThrownBy(() -> repository.save(List.of()))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    private MeleeWeapon createMeleeWeapon(int id) {
        MeleeWeapon meleeWeapon = new MeleeWeapon();
        meleeWeapon.setId(id);
        meleeWeapon.setPlayerUuid(PLAYER_UUID.toString());
        meleeWeapon.setMeleeWeaponName(MELEE_WEAPON_NAME);
        meleeWeapon.setItemSlot(MELEE_WEAPON_ITEM_SLOT);
        return meleeWeapon;
    }
}
