package nl.matsgemmeke.battlegrounds.storage.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import nl.matsgemmeke.battlegrounds.storage.Storage;
import nl.matsgemmeke.battlegrounds.storage.entity.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorageException;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class SqliteStorage implements Storage, PlayerStateStorage {

    @NotNull
    private final Dao<Gun, Integer> gunDao;

    public SqliteStorage(@NotNull Dao<Gun, Integer> gunDao) {
        this.gunDao = gunDao;
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
            throw new StateStorageException(e.getMessage());
        }
    }

    public void savePlayerState(@NotNull PlayerState playerState) {
        List<Gun> guns = playerState.gunStates().stream()
                .map(gunState -> this.convertGunStateToGun(playerState.playerUuid(), gunState))
                .toList();

        try {
            gunDao.create(guns);
        } catch (SQLException e) {
            throw new StateStorageException(e.getMessage());
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
