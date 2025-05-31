package nl.matsgemmeke.battlegrounds.storage.sqlite;

import com.j256.ormlite.dao.Dao;
import nl.matsgemmeke.battlegrounds.storage.Storage;
import nl.matsgemmeke.battlegrounds.storage.entity.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GamePlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorageException;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class SqliteStorage implements Storage, StateStorage {

    @NotNull
    private final Dao<Gun, Integer> gunDao;

    public SqliteStorage(@NotNull Dao<Gun, Integer> gunDao) {
        this.gunDao = gunDao;
    }

    public void saveGamePlayerState(@NotNull GamePlayerState gamePlayerState) {
        List<Gun> guns = gamePlayerState.gunStates().stream()
                .map(gunState -> this.convertGunStateToGun(gamePlayerState.playerUuid(), gunState))
                .toList();

        try {
            gunDao.create(guns);
        } catch (SQLException e) {
            throw new StateStorageException(e.getMessage());
        }
    }

    @NotNull
    private Gun convertGunStateToGun(@NotNull UUID playerUuid, @NotNull GunState gunState) {
        Gun gun = new Gun();
        gun.setPlayerUuid(playerUuid.toString());
        gun.setGunId(gunState.gunId());
        gun.setMagazineAmmo(gunState.magazineAmmo());
        gun.setReserveAmmo(gunState.reserveAmmo());
        return gun;
    }
}
