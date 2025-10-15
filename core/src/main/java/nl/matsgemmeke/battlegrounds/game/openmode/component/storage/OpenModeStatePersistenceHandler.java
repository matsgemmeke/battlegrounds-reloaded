package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class OpenModeStatePersistenceHandler implements StatePersistenceHandler {

    private final EquipmentRegistry equipmentRegistry;
    private final GunRegistry gunRegistry;
    private final Logger logger;
    private final PlayerRegistry playerRegistry;
    private final PlayerStateStorage playerStateStorage;
    private final WeaponCreator weaponCreator;

    @Inject
    public OpenModeStatePersistenceHandler(
            EquipmentRegistry equipmentRegistry,
            GunRegistry gunRegistry,
            @Named("Battlegrounds") Logger logger,
            PlayerRegistry playerRegistry,
            PlayerStateStorage playerStateStorage,
            WeaponCreator weaponCreator
    ) {
        this.logger = logger;
        this.playerStateStorage = playerStateStorage;
        this.weaponCreator = weaponCreator;
        this.equipmentRegistry = equipmentRegistry;
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
    }

    public void loadPlayerState(GamePlayer gamePlayer) {
        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
        PlayerState playerState = playerStateStorage.getPlayerState(playerUuid);

        playerState.gunStates().forEach(gunState -> this.loadGunState(gamePlayer, gunState));
        playerState.equipmentStates().forEach(equipmentState -> this.loadEquipmentState(gamePlayer, equipmentState));
    }

    private void loadGunState(GamePlayer gamePlayer, GunState gunState) {
        String gunName = gunState.gunName();

        if (!weaponCreator.gunExists(gunName)) {
            logger.severe("Attempted to load gun '%s' from the open mode of player %s, but it does not exist anymore".formatted(gunName, gamePlayer.getName()));
            return;
        }

        Gun gun = weaponCreator.createGun(gunName, gamePlayer);
        gun.getAmmunitionStorage().setMagazineAmmo(gunState.magazineAmmo());
        gun.getAmmunitionStorage().setReserveAmmo(gunState.reserveAmmo());
        gun.update();

        Player player = gamePlayer.getEntity();
        player.getInventory().setItem(gunState.itemSlot(), gun.getItemStack());
    }

    private void loadEquipmentState(GamePlayer gamePlayer, EquipmentState equipmentState) {
        String equipmentName = equipmentState.equipmentName();

        if (!weaponCreator.equipmentExists(equipmentName)) {
            logger.severe("Attempted to load equipment '%s' from the open mode of player %s, but it does not exist anymore".formatted(equipmentName, gamePlayer.getName()));
            return;
        }

        Equipment equipment = weaponCreator.createEquipment(equipmentName, gamePlayer);
        equipment.update();

        Player player = gamePlayer.getEntity();
        player.getInventory().setItem(equipmentState.itemSlot(), equipment.getItemStack());
    }

    public void savePlayerState(GamePlayer gamePlayer) {
        List<GunState> gunStates = gunRegistry.getAssignedGuns(gamePlayer).stream()
                .map(gun -> this.convertToGunState(gamePlayer, gun))
                .flatMap(Optional::stream)
                .toList();
        List<EquipmentState> equipmentStates = equipmentRegistry.getAssignedEquipment(gamePlayer).stream()
                .map(equipment -> this.convertToEquipmentState(gamePlayer, equipment))
                .flatMap(Optional::stream)
                .toList();

        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
        PlayerState playerState = new PlayerState(playerUuid, gunStates, equipmentStates);

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

    private Optional<GunState> convertToGunState(GamePlayer gamePlayer, Gun gun) {
        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
        String gunName = gun.getName();
        int magazineAmmo = gun.getAmmunitionStorage().getMagazineAmmo();
        int reserveAmmo = gun.getAmmunitionStorage().getReserveAmmo();
        ItemStack itemStack = gun.getItemStack();

        if (itemStack == null) {
            return Optional.empty();
        }

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(gun);
        return itemSlot.map(itemSlotValue -> new GunState(playerUuid, gunName, magazineAmmo, reserveAmmo, itemSlotValue));
    }

    private Optional<EquipmentState> convertToEquipmentState(GamePlayer gamePlayer, Equipment equipment) {
        UUID playerUuid = gamePlayer.getEntity().getUniqueId();
        String equipmentName = equipment.getName();
        ItemStack itemStack = equipment.getItemStack();

        if (itemStack == null) {
            return Optional.empty();
        }

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(equipment);
        return itemSlot.map(integer -> new EquipmentState(playerUuid, equipmentName, integer));
    }
}
