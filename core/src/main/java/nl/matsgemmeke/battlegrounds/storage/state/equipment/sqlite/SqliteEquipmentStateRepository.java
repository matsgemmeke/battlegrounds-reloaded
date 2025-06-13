package nl.matsgemmeke.battlegrounds.storage.state.equipment.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentStateRepository;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SqliteEquipmentStateRepository implements EquipmentStateRepository {

    @NotNull
    private final Dao<Equipment, Integer> equipmentDao;

    public SqliteEquipmentStateRepository(@NotNull Dao<Equipment, Integer> equipmentDao) {
        this.equipmentDao = equipmentDao;
    }

    public void deleteByPlayerUuid(@NotNull UUID playerUuid) {
        try {
            PreparedQuery<Equipment> statement = equipmentDao.queryBuilder()
                    .where().eq("player_uuid", playerUuid.toString())
                    .prepare();
            List<Equipment> equipment = equipmentDao.query(statement);

            equipmentDao.delete(equipment);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    @NotNull
    public List<EquipmentState> findByPlayerUuid(@NotNull UUID playerUuid) {
        try {
            PreparedQuery<Equipment> statement = equipmentDao.queryBuilder()
                    .where().eq("player_uuid", playerUuid.toString())
                    .prepare();

            return equipmentDao.query(statement).stream().map(this::convertEquipmentToEquipmentState).toList();
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    public void save(@NotNull Collection<EquipmentState> equipmentStates) {
        List<Equipment> equipment = equipmentStates.stream().map(this::convertEquipmentStateToEquipment).toList();

        try {
            equipmentDao.create(equipment);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    @NotNull
    private EquipmentState convertEquipmentToEquipmentState(@NotNull Equipment equipment) {
        UUID playerUuid = UUID.fromString(equipment.getPlayerUuid());

        return new EquipmentState(playerUuid, equipment.getEquipmentId(), equipment.getItemSlot());
    }

    @NotNull
    private Equipment convertEquipmentStateToEquipment(@NotNull EquipmentState equipmentState) {
        Equipment equipment = new Equipment();
        equipment.setPlayerUuid(equipmentState.playerUuid().toString());
        equipment.setEquipmentId(equipmentState.equipmentId());
        equipment.setItemSlot(equipmentState.itemSlot());
        return equipment;
    }
}
