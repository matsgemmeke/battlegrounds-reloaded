package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.GamePlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class OpenModeStatePersistenceHandler implements StatePersistenceHandler {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();

    @NotNull
    private final GunRegistry gunRegistry;
    @NotNull
    private final Logger logger;
    @NotNull
    private final PlayerRegistry playerRegistry;
    @NotNull
    private final StateStorage stateStorage;
    @NotNull
    private final WeaponCreator weaponCreator;

    @Inject
    public OpenModeStatePersistenceHandler(
            @NotNull Logger logger,
            @Named("SQLite") @NotNull StateStorage stateStorage,
            @NotNull WeaponCreator weaponCreator,
            @Assisted @NotNull GunRegistry gunRegistry,
            @Assisted @NotNull PlayerRegistry playerRegistry
    ) {
        this.logger = logger;
        this.stateStorage = stateStorage;
        this.weaponCreator = weaponCreator;
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
    }

    public void loadState(@NotNull GamePlayer gamePlayer) {
        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
        GamePlayerState gamePlayerState = stateStorage.findGamePlayerStateByPlayerUuid(playerUuid);

        for (GunState gunState : gamePlayerState.gunStates()) {
            this.loadGunState(gamePlayer, gunState);
        }
    }

    private void loadGunState(@NotNull GamePlayer gamePlayer, @NotNull GunState gunState) {
        String gunId = gunState.gunId();

        if (!weaponCreator.gunExists(gunId)) {
            logger.severe("Attempted to load gun '%s' from the open mode of player %s, but it does not exist anymore".formatted(gunId, gamePlayer.getName()));
            return;
        }

        Gun gun = weaponCreator.createGun(gunId, gamePlayer, GAME_KEY);
        gun.getAmmunitionStorage().setMagazineAmmo(gunState.magazineAmmo());
        gun.getAmmunitionStorage().setReserveAmmo(gunState.reserveAmmo());
        gun.update();

        Player player = gamePlayer.getEntity();
        player.getInventory().setItem(gunState.itemSlot(), gun.getItemStack());
    }

    public void saveState() {
        playerRegistry.getAll().forEach(this::saveGamePlayerState);
    }

    private void saveGamePlayerState(@NotNull GamePlayer gamePlayer) {
        List<GunState> gunStates = gunRegistry.getAssignedItems(gamePlayer).stream()
                .map(gun -> this.convertToGunState(gamePlayer, gun))
                .flatMap(Optional::stream)
                .toList();

        UUID uuid = gamePlayer.getEntity().getUniqueId();
        GamePlayerState gamePlayerState = new GamePlayerState(uuid, gunStates);

        stateStorage.saveGamePlayerState(gamePlayerState);
    }

    @NotNull
    private Optional<GunState> convertToGunState(@NotNull GamePlayer gamePlayer, @NotNull Gun gun) {
        String id = gun.getId();
        int magazineAmmo = gun.getAmmunitionStorage().getMagazineAmmo();
        int reserveAmmo = gun.getAmmunitionStorage().getReserveAmmo();
        ItemStack itemStack = gun.getItemStack();

        if (itemStack == null) {
            logger.severe("Cannot save state for gun %s of player %s, since it has no item stack".formatted(id, gamePlayer.getName()));
            return Optional.empty();
        }

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(gun.getItemStack());

        if (itemSlot.isEmpty()) {
            logger.severe("Cannot save state for gun %s of player %s, since its item slot cannot be determined".formatted(id, gamePlayer.getName()));
            return Optional.empty();
        }

        return Optional.of(new GunState(id, magazineAmmo, reserveAmmo, itemSlot.get()));
    }
}
