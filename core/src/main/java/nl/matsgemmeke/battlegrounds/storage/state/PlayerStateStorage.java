package nl.matsgemmeke.battlegrounds.storage.state;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentStateRepository;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunStateRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PlayerStateStorage {

    @NotNull
    private final EquipmentStateRepository equipmentStateRepository;
    @NotNull
    private final GunStateRepository gunStateRepository;

    @Inject
    public PlayerStateStorage(@NotNull EquipmentStateRepository equipmentStateRepository, @NotNull GunStateRepository gunStateRepository) {
        this.equipmentStateRepository = equipmentStateRepository;
        this.gunStateRepository = gunStateRepository;
    }

    public void deletePlayerState(@NotNull UUID playerUuid) {
        gunStateRepository.deleteByPlayerUuid(playerUuid);
        equipmentStateRepository.deleteByPlayerUuid(playerUuid);
    }

    @NotNull
    public PlayerState getPlayerState(@NotNull UUID playerUuid) {
        List<GunState> gunStates = gunStateRepository.findByPlayerUuid(playerUuid);
        List<EquipmentState> equipmentStates = equipmentStateRepository.findByPlayerUuid(playerUuid);

        return new PlayerState(playerUuid, gunStates, equipmentStates);
    }

    public void savePlayerState(@NotNull PlayerState playerState) {
        gunStateRepository.save(playerState.gunStates());
        equipmentStateRepository.save(playerState.equipmentStates());
    }
}
