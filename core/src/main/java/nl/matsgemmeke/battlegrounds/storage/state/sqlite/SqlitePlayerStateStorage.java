package nl.matsgemmeke.battlegrounds.storage.state.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import nl.matsgemmeke.battlegrounds.storage.entity.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class SqlitePlayerStateStorage implements PlayerStateStorage {

    @NotNull
    private final Dao<Gun, Integer> gunDao;

    public SqlitePlayerStateStorage(@NotNull Dao<Gun, Integer> gunDao) {
        this.gunDao = gunDao;
    }

    public void deletePlayerState(@NotNull UUID playerUuid) {
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

    @NotNull
    public PlayerState findPlayerStateByPlayerUuid(@NotNull UUID playerUuid) {
        try {
            PreparedQuery<Gun> statement = gunDao.queryBuilder()
                    .where().eq("player_uuid", playerUuid.toString())
                    .prepare();
            List<GunState> gunStates = gunDao.query(statement).stream()
                    .map(this::convertGunToGunState)
                    .toList();

            return new PlayerState(playerUuid, gunStates);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    public void savePlayerState(@NotNull PlayerState playerState) {
        UUID playerUuid = playerState.playerUuid();
        List<Gun> guns = playerState.gunStates().stream()
                .map(gunState -> this.convertGunStateToGun(playerUuid, gunState))
                .toList();

        try {
            gunDao.create(guns);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    @NotNull
    private GunState convertGunToGunState(@NotNull Gun gun) {
        return new GunState(gun.getGunId(), gun.getMagazineAmmo(), gun.getReserveAmmo(), gun.getItemSlot());
    }

    @NotNull
    private Gun convertGunStateToGun(@NotNull UUID playerUuid, @NotNull GunState gunState) {
        Gun gun = new Gun();
        gun.setPlayerUuid(playerUuid.toString());
        gun.setGunId(gunState.gunId());
        gun.setMagazineAmmo(gunState.magazineAmmo());
        gun.setReserveAmmo(gunState.reserveAmmo());
        gun.setItemSlot(gunState.itemSlot());
        return gun;
    }
}
