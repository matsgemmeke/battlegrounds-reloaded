package nl.matsgemmeke.battlegrounds.storage.state;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentStateRepository;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunStateRepository;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponState;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponStateRepository;

import java.util.List;
import java.util.UUID;

public class PlayerStateStorage {

    private final EquipmentStateRepository equipmentStateRepository;
    private final GunStateRepository gunStateRepository;
    private final MeleeWeaponStateRepository meleeWeaponStateRepository;

    @Inject
    public PlayerStateStorage(EquipmentStateRepository equipmentStateRepository, GunStateRepository gunStateRepository, MeleeWeaponStateRepository meleeWeaponStateRepository) {
        this.equipmentStateRepository = equipmentStateRepository;
        this.gunStateRepository = gunStateRepository;
        this.meleeWeaponStateRepository = meleeWeaponStateRepository;
    }

    public void deletePlayerState(UUID playerUuid) {
        gunStateRepository.deleteByPlayerUuid(playerUuid);
        equipmentStateRepository.deleteByPlayerUuid(playerUuid);
        meleeWeaponStateRepository.deleteByPlayerUuid(playerUuid);
    }

    public PlayerState getPlayerState(UUID playerUuid) {
        List<GunState> gunStates = gunStateRepository.findByPlayerUuid(playerUuid);
        List<EquipmentState> equipmentStates = equipmentStateRepository.findByPlayerUuid(playerUuid);
        List<MeleeWeaponState> meleeWeaponStates = meleeWeaponStateRepository.findByPlayerUuid(playerUuid);

        return new PlayerState(playerUuid, gunStates, equipmentStates, meleeWeaponStates);
    }

    public void savePlayerState(PlayerState playerState) {
        gunStateRepository.save(playerState.gunStates());
        equipmentStateRepository.save(playerState.equipmentStates());
        meleeWeaponStateRepository.save(playerState.meleeWeaponStates());
    }
}
