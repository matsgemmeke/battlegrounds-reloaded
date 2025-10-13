package nl.matsgemmeke.battlegrounds.storage.state.gun.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunStateRepository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SqliteGunStateRepository implements GunStateRepository {

    private final Dao<Gun, Integer> gunDao;

    public SqliteGunStateRepository(Dao<Gun, Integer> gunDao) {
        this.gunDao = gunDao;
    }

    public void deleteByPlayerUuid(UUID playerUuid) {
        try {
            PreparedQuery<Gun> statement = gunDao.queryBuilder()
                    .where().eq("player_uuid", playerUuid.toString())
                    .prepare();
            List<Gun> guns = gunDao.query(statement);

            gunDao.delete(guns);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    public List<GunState> findByPlayerUuid(UUID playerUuid) {
        try {
            PreparedQuery<Gun> statement = gunDao.queryBuilder()
                    .where().eq("player_uuid", playerUuid.toString())
                    .prepare();

            return gunDao.query(statement).stream().map(this::convertGunToGunState).toList();
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    public void save(Collection<GunState> gunStates) {
        List<Gun> guns = gunStates.stream().map(this::convertGunStateToGun).toList();

        try {
            gunDao.create(guns);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    private GunState convertGunToGunState(Gun gun) {
        UUID playerUuid = UUID.fromString(gun.getPlayerUuid());

        return new GunState(playerUuid, gun.getGunName(), gun.getMagazineAmmo(), gun.getReserveAmmo(), gun.getItemSlot());
    }

    private Gun convertGunStateToGun(GunState gunState) {
        Gun gun = new Gun();
        gun.setPlayerUuid(gunState.playerUuid().toString());
        gun.setGunName(gunState.gunName());
        gun.setMagazineAmmo(gunState.magazineAmmo());
        gun.setReserveAmmo(gunState.reserveAmmo());
        gun.setItemSlot(gunState.itemSlot());
        return gun;
    }
}
