package nl.matsgemmeke.battlegrounds.storage.state;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunStateRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PlayerStateStorage {

    @NotNull
    private final GunStateRepository gunStateRepository;

    @Inject
    public PlayerStateStorage(@NotNull GunStateRepository gunStateRepository) {
        this.gunStateRepository = gunStateRepository;
    }

    public void deletePlayerState(@NotNull UUID playerUuid) {
        gunStateRepository.deleteByPlayerUuid(playerUuid);
    }

    @NotNull
    public PlayerState getPlayerState(@NotNull UUID playerUuid) {
        List<GunState> gunStates = gunStateRepository.findByPlayerUuid(playerUuid);

        return new PlayerState(playerUuid, gunStates);
    }

    public void savePlayerState(@NotNull PlayerState playerState) {
        gunStateRepository.save(playerState.gunStates());
    }
}
