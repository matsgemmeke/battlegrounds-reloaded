package nl.matsgemmeke.battlegrounds.storage.state;

import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PlayerStateStorageTest {

    private static final UUID PLAYER_UUID = UUID.randomUUID();

    private GunStateRepository gunStateRepository;

    @BeforeEach
    public void setUp() {
        gunStateRepository = mock(GunStateRepository.class);
    }

    @Test
    public void deletePlayerStateDeletesPlayerDataFromRepositories() {
        PlayerStateStorage playerStateStorage = new PlayerStateStorage(gunStateRepository);
        playerStateStorage.deletePlayerState(PLAYER_UUID);

        verify(gunStateRepository).deleteByPlayerUuid(PLAYER_UUID);
    }

    @Test
    public void getPlayerStateReturnsPlayerStateWithDataFromRepositories() {
        GunState gunState = new GunState(PLAYER_UUID, "TEST_GUN", 10, 20, 5);

        when(gunStateRepository.findByPlayerUuid(PLAYER_UUID)).thenReturn(List.of(gunState));

        PlayerStateStorage playerStateStorage = new PlayerStateStorage(gunStateRepository);
        PlayerState playerState = playerStateStorage.getPlayerState(PLAYER_UUID);

        assertThat(playerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(playerState.gunStates()).containsExactly(gunState);
    }

    @Test
    public void savePlayerStateSendsDataToCorrespondingRepositories() {
        GunState gunState = new GunState(PLAYER_UUID, "TEST_GUN", 10, 20, 5);
        List<GunState> gunStates = List.of(gunState);
        PlayerState playerState = new PlayerState(PLAYER_UUID, gunStates);

        PlayerStateStorage playerStateStorage = new PlayerStateStorage(gunStateRepository);
        playerStateStorage.savePlayerState(playerState);

        verify(gunStateRepository).save(gunStates);
    }
}
