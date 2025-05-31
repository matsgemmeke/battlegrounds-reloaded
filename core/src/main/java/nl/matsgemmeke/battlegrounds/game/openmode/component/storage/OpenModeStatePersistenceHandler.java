package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GamePlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class OpenModeStatePersistenceHandler implements StatePersistenceHandler {

    @NotNull
    private final GunRegistry gunRegistry;
    @NotNull
    private final PlayerRegistry playerRegistry;
    @NotNull
    private final StateStorage stateStorage;

    @Inject
    public OpenModeStatePersistenceHandler(
            @Named("SQLite") @NotNull StateStorage stateStorage,
            @Assisted @NotNull GunRegistry gunRegistry,
            @Assisted @NotNull PlayerRegistry playerRegistry
    ) {
        this.stateStorage = stateStorage;
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
    }

    public void saveState() {
        playerRegistry.getAll().forEach(this::saveGamePlayerState);
    }

    private void saveGamePlayerState(@NotNull GamePlayer gamePlayer) {
        UUID uuid = gamePlayer.getEntity().getUniqueId();
        List<GunState> gunStates = gunRegistry.getAssignedItems(gamePlayer).stream()
                .map(this::convertToGunState)
                .toList();

        GamePlayerState gamePlayerState = new GamePlayerState(uuid, gunStates);

        stateStorage.saveGamePlayerState(gamePlayerState);
    }

    @NotNull
    private GunState convertToGunState(@NotNull Gun gun) {
        String id = gun.getId();
        int magazineAmmo = gun.getAmmunitionStorage().getMagazineAmmo();
        int reserveAmmo = gun.getAmmunitionStorage().getReserveAmmo();

        return new GunState(id, magazineAmmo, reserveAmmo);
    }
}
