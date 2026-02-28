package nl.matsgemmeke.battlegrounds.storage.state;

import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentStateRepository;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunStateRepository;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponState;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponStateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerStateStorageTest {

    private static final UUID PLAYER_UUID = UUID.randomUUID();

    @Mock
    private EquipmentStateRepository equipmentStateRepository;
    @Mock
    private GunStateRepository gunStateRepository;
    @Mock
    private MeleeWeaponStateRepository meleeWeaponStateRepository;
    @InjectMocks
    private PlayerStateStorage playerStateStorage;

    @Test
    @DisplayName("deletePlayerState delete player data from repositories")
    void deletePlayerState_deletesData() {
        playerStateStorage.deletePlayerState(PLAYER_UUID);

        verify(gunStateRepository).deleteByPlayerUuid(PLAYER_UUID);
        verify(equipmentStateRepository).deleteByPlayerUuid(PLAYER_UUID);
        verify(meleeWeaponStateRepository).deleteByPlayerUuid(PLAYER_UUID);
    }

    @Test
    @DisplayName("getPlayerState returns PlayerState with data from repositories")
    void getPlayerState_returnsPlayerState() {
        GunState gunState = new GunState(PLAYER_UUID, "TEST_GUN", 10, 20, 5);
        EquipmentState equipmentState = new EquipmentState(PLAYER_UUID, "TEST_EQUIPMENT", 6);
        MeleeWeaponState meleeWeaponState = new MeleeWeaponState(PLAYER_UUID, "TEST_MELEE_WEAPON", 1, 2, 7);

        when(gunStateRepository.findByPlayerUuid(PLAYER_UUID)).thenReturn(List.of(gunState));
        when(equipmentStateRepository.findByPlayerUuid(PLAYER_UUID)).thenReturn(List.of(equipmentState));
        when(meleeWeaponStateRepository.findByPlayerUuid(PLAYER_UUID)).thenReturn(List.of(meleeWeaponState));

        PlayerState playerState = playerStateStorage.getPlayerState(PLAYER_UUID);

        assertThat(playerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(playerState.gunStates()).containsExactly(gunState);
        assertThat(playerState.equipmentStates()).containsExactly(equipmentState);
    }

    @Test
    @DisplayName("savePlayerState sends data to corresponding repositories")
    void savePlayerState_savesDataToRepositories() {
        GunState gunState = new GunState(PLAYER_UUID, "TEST_GUN", 10, 20, 5);
        List<GunState> gunStates = List.of(gunState);
        EquipmentState equipmentState = new EquipmentState(PLAYER_UUID, "TEST_EQUIPMENT", 6);
        List<EquipmentState> equipmentStates = List.of(equipmentState);
        MeleeWeaponState meleeWeaponState = new MeleeWeaponState(PLAYER_UUID, "TEST_MELEE_WEAPON", 1, 2, 7);
        List<MeleeWeaponState> meleeWeaponStates = List.of(meleeWeaponState);

        PlayerState playerState = new PlayerState(PLAYER_UUID, gunStates, equipmentStates, meleeWeaponStates);

        playerStateStorage.savePlayerState(playerState);

        verify(gunStateRepository).save(gunStates);
        verify(equipmentStateRepository).save(equipmentStates);
        verify(meleeWeaponStateRepository).save(meleeWeaponStates);
    }
}
