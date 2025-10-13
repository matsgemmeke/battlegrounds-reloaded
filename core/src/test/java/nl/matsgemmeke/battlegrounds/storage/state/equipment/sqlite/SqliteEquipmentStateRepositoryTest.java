package nl.matsgemmeke.battlegrounds.storage.state.equipment.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public class SqliteEquipmentStateRepositoryTest {

    private static final UUID PLAYER_UUID = UUID.randomUUID();
    private static final String EQUIPMENT_NAME = "Test Equipment";
    private static final int EQUIPMENT_ITEM_SLOT = 5;

    private Dao<Equipment, Integer> equipmentDao;

    @BeforeEach
    public void setUp() throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:h2:mem:test");
        TableUtils.dropTable(connectionSource, Equipment.class, true);
        TableUtils.createTable(connectionSource, Equipment.class);

        equipmentDao = DaoManager.createDao(connectionSource, Equipment.class);
    }

    @Test
    public void deleteByPlayerUuidDeletesDataFromDao() throws SQLException {
        int id = 1;
        Equipment equipment = this.createEquipment(id);

        equipmentDao.create(equipment);

        SqliteEquipmentStateRepository repository = new SqliteEquipmentStateRepository(equipmentDao);
        repository.deleteByPlayerUuid(PLAYER_UUID);

        List<Equipment> savedEquipment = equipmentDao.queryForAll();

        assertThat(savedEquipment).isEmpty();
    }

    @Test
    public void deleteByPlayerUuidThrowsPlayerStateStorageExceptionWhenFailingToDeleteData() throws SQLException {
        Dao<Equipment, Integer> equipmentDao = mock(RETURNS_DEEP_STUBS);
        when(equipmentDao.queryBuilder().where().eq("player_uuid", PLAYER_UUID.toString()).prepare()).thenThrow(new SQLException("error"));

        SqliteEquipmentStateRepository repository = new SqliteEquipmentStateRepository(equipmentDao);

        assertThatThrownBy(() -> repository.deleteByPlayerUuid(PLAYER_UUID))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    @Test
    public void findByPlayerUuidReturnsEquipmentStatesWithDataFromDao() throws SQLException {
        int id = 1;
        Equipment equipment = this.createEquipment(id);

        equipmentDao.create(equipment);

        SqliteEquipmentStateRepository repository = new SqliteEquipmentStateRepository(equipmentDao);
        List<EquipmentState> equipmentStates = repository.findByPlayerUuid(PLAYER_UUID);

        assertThat(equipmentStates).hasSize(1);
        assertThat(equipmentStates.get(0).playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(equipmentStates.get(0).equipmentName()).isEqualTo(EQUIPMENT_NAME);
        assertThat(equipmentStates.get(0).itemSlot()).isEqualTo(EQUIPMENT_ITEM_SLOT);
    }

    @Test
    public void findByPlayerUuidThrowsPlayerStateStorageExceptionWhenFailingToReadData() throws SQLException {
        Dao<Equipment, Integer> equipmentDao = mock(RETURNS_DEEP_STUBS);
        when(equipmentDao.queryBuilder().where().eq("player_uuid", PLAYER_UUID.toString()).prepare()).thenThrow(new SQLException("error"));

        SqliteEquipmentStateRepository repository = new SqliteEquipmentStateRepository(equipmentDao);

        assertThatThrownBy(() -> repository.findByPlayerUuid(PLAYER_UUID))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    @Test
    public void saveSavesDataFromGivenEquipmentStatesToDao() throws SQLException {
        EquipmentState equipmentState = new EquipmentState(PLAYER_UUID, EQUIPMENT_NAME, EQUIPMENT_ITEM_SLOT);
        List<EquipmentState> equipmentStates = List.of(equipmentState);

        SqliteEquipmentStateRepository repository = new SqliteEquipmentStateRepository(equipmentDao);
        repository.save(equipmentStates);

        List<Equipment> savedEquipment = equipmentDao.queryForAll();

        assertThat(savedEquipment).hasSize(1);
        assertThat(savedEquipment.get(0).getPlayerUuid()).isEqualTo(PLAYER_UUID.toString());
        assertThat(savedEquipment.get(0).getEquipmentName()).isEqualTo(EQUIPMENT_NAME);
        assertThat(savedEquipment.get(0).getItemSlot()).isEqualTo(EQUIPMENT_ITEM_SLOT);
    }

    @Test
    public void saveThrowsStateStorageExceptionWhenFailingToSaveData() throws SQLException {
        Dao<Equipment, Integer> equipmentDao = mock(RETURNS_DEEP_STUBS);
        when(equipmentDao.create(anyCollection())).thenThrow(new SQLException("error"));

        SqliteEquipmentStateRepository repository = new SqliteEquipmentStateRepository(equipmentDao);

        assertThatThrownBy(() -> repository.save(List.of()))
                .isInstanceOf(PlayerStateStorageException.class)
                .hasMessage("error");
    }

    private Equipment createEquipment(int id) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setPlayerUuid(PLAYER_UUID.toString());
        equipment.setEquipmentName(EQUIPMENT_NAME);
        equipment.setItemSlot(EQUIPMENT_ITEM_SLOT);
        return equipment;
    }
}
