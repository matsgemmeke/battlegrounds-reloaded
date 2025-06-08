package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
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
    private final PlayerStateStorage playerStateStorage;
    @NotNull
    private final WeaponCreator weaponCreator;

    @Inject
    public OpenModeStatePersistenceHandler(
            @NotNull Logger logger,
            @NotNull PlayerStateStorage playerStateStorage,
            @NotNull WeaponCreator weaponCreator,
            @Assisted @NotNull GunRegistry gunRegistry,
            @Assisted @NotNull PlayerRegistry playerRegistry
    ) {
        this.logger = logger;
        this.playerStateStorage = playerStateStorage;
        this.weaponCreator = weaponCreator;
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
    }

    public void loadPlayerState(@NotNull GamePlayer gamePlayer) {
        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
        PlayerState playerState = playerStateStorage.getPlayerState(playerUuid);

        for (GunState gunState : playerState.gunStates()) {
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

    public void savePlayerState(@NotNull GamePlayer gamePlayer) {
        List<GunState> gunStates = gunRegistry.getAssignedItems(gamePlayer).stream()
                .map(gun -> this.convertToGunState(gamePlayer, gun))
                .flatMap(Optional::stream)
                .toList();

        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
        PlayerState playerState = new PlayerState(playerUuid, gunStates);

        playerStateStorage.deletePlayerState(playerUuid);
        playerStateStorage.savePlayerState(playerState);
    }

    public void saveState() {
        logger.info("Saving current state of open mode");

        int failedSaves = 0;
        String errorMessage = null;

        for (GamePlayer gamePlayer : playerRegistry.getAll()) {
            try {
                this.savePlayerState(gamePlayer);
            } catch (PlayerStateStorageException e) {
                failedSaves++;
                errorMessage = e.getMessage();
            }
        }

        if (failedSaves > 0) {
            logger.severe("Failed to save current state of %s player(s). Caused by: %s".formatted(failedSaves, errorMessage));
        }
    }

    @NotNull
    private Optional<GunState> convertToGunState(@NotNull GamePlayer gamePlayer, @NotNull Gun gun) {
        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
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

        return Optional.of(new GunState(playerUuid, id, magazineAmmo, reserveAmmo, itemSlot.get()));
    }
}
