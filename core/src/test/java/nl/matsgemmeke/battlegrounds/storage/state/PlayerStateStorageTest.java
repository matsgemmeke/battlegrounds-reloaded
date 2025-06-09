package nl.matsgemmeke.battlegrounds.storage.state;

import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentStateRepository;
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

    private EquipmentStateRepository equipmentStateRepository;
    private GunStateRepository gunStateRepository;

    @BeforeEach
    public void setUp() {
        equipmentStateRepository = mock(EquipmentStateRepository.class);
        gunStateRepository = mock(GunStateRepository.class);
    }

    @Test
    public void deletePlayerStateDeletesPlayerDataFromRepositories() {
        PlayerStateStorage playerStateStorage = new PlayerStateStorage(equipmentStateRepository, gunStateRepository);
        playerStateStorage.deletePlayerState(PLAYER_UUID);

        verify(gunStateRepository).deleteByPlayerUuid(PLAYER_UUID);
        verify(equipmentStateRepository).deleteByPlayerUuid(PLAYER_UUID);
    }

    @Test
    public void getPlayerStateReturnsPlayerStateWithDataFromRepositories() {
        GunState gunState = new GunState(PLAYER_UUID, "TEST_GUN", 10, 20, 5);
        EquipmentState equipmentState = new EquipmentState(PLAYER_UUID, "TEST_EQUIPMENT", 6);

        when(gunStateRepository.findByPlayerUuid(PLAYER_UUID)).thenReturn(List.of(gunState));
        when(equipmentStateRepository.findByPlayerUuid(PLAYER_UUID)).thenReturn(List.of(equipmentState));

        PlayerStateStorage playerStateStorage = new PlayerStateStorage(equipmentStateRepository, gunStateRepository);
        PlayerState playerState = playerStateStorage.getPlayerState(PLAYER_UUID);

        assertThat(playerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(playerState.gunStates()).containsExactly(gunState);
        assertThat(playerState.equipmentStates()).containsExactly(equipmentState);
    }

    @Test
    public void savePlayerStateSendsDataToCorrespondingRepositories() {
        GunState gunState = new GunState(PLAYER_UUID, "TEST_GUN", 10, 20, 5);
        List<GunState> gunStates = List.of(gunState);
        EquipmentState equipmentState = new EquipmentState(PLAYER_UUID, "TEST_EQUIPMENT", 6);
        List<EquipmentState> equipmentStates = List.of(equipmentState);
        PlayerState playerState = new PlayerState(PLAYER_UUID, gunStates, equipmentStates);

        PlayerStateStorage playerStateStorage = new PlayerStateStorage(equipmentStateRepository, gunStateRepository);
        playerStateStorage.savePlayerState(playerState);

        verify(gunStateRepository).save(gunStates);
        verify(equipmentStateRepository).save(equipmentStates);
    }
}
