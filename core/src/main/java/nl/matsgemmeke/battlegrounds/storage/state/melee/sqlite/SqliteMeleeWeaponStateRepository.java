package nl.matsgemmeke.battlegrounds.storage.state.melee.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponState;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponStateRepository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SqliteMeleeWeaponStateRepository implements MeleeWeaponStateRepository {

    private final Dao<MeleeWeapon, Integer> meleeWeaponDao;

    public SqliteMeleeWeaponStateRepository(Dao<MeleeWeapon, Integer> meleeWeaponDao) {
        this.meleeWeaponDao = meleeWeaponDao;
    }

    @Override
    public void deleteByPlayerUuid(UUID playerUuid) {
        try {
            PreparedQuery<MeleeWeapon> statement = meleeWeaponDao.queryBuilder()
                    .where().eq("player_uuid", playerUuid.toString())
                    .prepare();
            List<MeleeWeapon> meleeWeapons = meleeWeaponDao.query(statement);

            meleeWeaponDao.delete(meleeWeapons);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    @Override
    public List<MeleeWeaponState> findByPlayerUuid(UUID playerUuid) {
        try {
            PreparedQuery<MeleeWeapon> statement = meleeWeaponDao.queryBuilder()
                    .where().eq("player_uuid", playerUuid.toString())
                    .prepare();

            return meleeWeaponDao.query(statement).stream().map(this::convertMeleeWeaponToMeleeWeaponState).toList();
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    @Override
    public void save(Collection<MeleeWeaponState> equipmentStates) {
        List<MeleeWeapon> meleeWeapons = equipmentStates.stream().map(this::convertMeleeWeaponStateToMeleeWeapon).toList();

        try {
            meleeWeaponDao.create(meleeWeapons);
        } catch (SQLException e) {
            throw new PlayerStateStorageException(e.getMessage());
        }
    }

    private MeleeWeaponState convertMeleeWeaponToMeleeWeaponState(MeleeWeapon meleeWeapon) {
        UUID playerUuid = UUID.fromString(meleeWeapon.getPlayerUuid());

        return new MeleeWeaponState(playerUuid, meleeWeapon.getMeleeWeaponName(), meleeWeapon.getItemSlot());
    }

    private MeleeWeapon convertMeleeWeaponStateToMeleeWeapon(MeleeWeaponState meleeWeaponState) {
        MeleeWeapon meleeWeapon = new MeleeWeapon();
        meleeWeapon.setPlayerUuid(meleeWeaponState.playerUuid().toString());
        meleeWeapon.setMeleeWeaponName(meleeWeaponState.meleeWeaponName());
        meleeWeapon.setItemSlot(meleeWeaponState.itemSlot());
        return meleeWeapon;
    }
}
