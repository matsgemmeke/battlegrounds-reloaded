package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GamePlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OpenModeStatePersistenceHandlerTest {

    private GunRegistry gunRegistry;
    private PlayerRegistry playerRegistry;
    private StateStorage stateStorage;

    @BeforeEach
    public void setUp() {
        gunRegistry = mock(GunRegistry.class);
        playerRegistry = mock(PlayerRegistry.class);
        stateStorage = mock(StateStorage.class);
    }

    @Test
    public void saveStateCollectsDataFromRegistryInstancesAndSavesStateToStateStorage() throws StateStorageException {
        String gunId = "TEST_GUN";
        int magazineAmmo = 10;
        int reserveAmmo = 20;
        UUID playerUuid = UUID.randomUUID();

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(playerUuid);

        Gun gun = mock(Gun.class, RETURNS_DEEP_STUBS);
        when(gun.getId()).thenReturn(gunId);
        when(gun.getAmmunitionStorage().getMagazineAmmo()).thenReturn(magazineAmmo);
        when(gun.getAmmunitionStorage().getReserveAmmo()).thenReturn(reserveAmmo);

        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(gun));
        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(stateStorage, gunRegistry, playerRegistry);
        statePersistenceHandler.saveState();

        ArgumentCaptor<GamePlayerState> gamePlayerStateCaptor = ArgumentCaptor.forClass(GamePlayerState.class);
        verify(stateStorage).saveGamePlayerState(gamePlayerStateCaptor.capture());

        GamePlayerState savedState = gamePlayerStateCaptor.getValue();

        assertThat(savedState.playerUuid()).isEqualTo(playerUuid);
        assertThat(savedState.gunStates()).hasSize(1);
        assertThat(savedState.gunStates().get(0).gunId()).isEqualTo(gunId);
        assertThat(savedState.gunStates().get(0).magazineAmmo()).isEqualTo(magazineAmmo);
        assertThat(savedState.gunStates().get(0).reserveAmmo()).isEqualTo(reserveAmmo);
    }
}
