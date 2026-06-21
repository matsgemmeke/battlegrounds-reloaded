package nl.matsgemmeke.battlegrounds.game.freeplay.component.storage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.registry.ItemSpecRegistry;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponState;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class FreeplayStatePersistenceHandler implements StatePersistenceHandler {

    private final EquipmentRegistry equipmentRegistry;
    private final GunRegistry gunRegistry;
    private final ItemCreator itemCreator;
    private final ItemSpecRegistry itemSpecRegistry;
    private final Logger logger;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final PlayerRegistry playerRegistry;
    private final PlayerStateStorage playerStateStorage;

    @Inject
    public FreeplayStatePersistenceHandler(
            EquipmentRegistry equipmentRegistry,
            GunRegistry gunRegistry,
            ItemCreator itemCreator,
            ItemSpecRegistry itemSpecRegistry,
            @Named("Battlegrounds") Logger logger,
            MeleeWeaponRegistry meleeWeaponRegistry,
            PlayerRegistry playerRegistry,
            PlayerStateStorage playerStateStorage
    ) {
        this.itemCreator = itemCreator;
        this.itemSpecRegistry = itemSpecRegistry;
        this.logger = logger;
        this.playerStateStorage = playerStateStorage;
        this.equipmentRegistry = equipmentRegistry;
        this.gunRegistry = gunRegistry;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.playerRegistry = playerRegistry;
    }

    public void loadPlayerState(GamePlayer gamePlayer) {
        UUID uniqueId = gamePlayer.getUniqueId();
        PlayerState playerState = playerStateStorage.getPlayerState(uniqueId);

        playerState.gunStates().forEach(gunState -> this.loadGunState(gamePlayer, gunState));
        playerState.equipmentStates().forEach(equipmentState -> this.loadEquipmentState(gamePlayer, equipmentState));
        playerState.meleeWeaponStates().forEach(meleeWeaponState -> this.loadMeleeWeaponState(gamePlayer, meleeWeaponState));
    }

    private void loadGunState(GamePlayer gamePlayer, GunState gunState) {
        String gunName = gunState.gunName();

        if (itemSpecRegistry.getGunSpec(gunName).isEmpty()) {
            logger.severe("Attempted to load gun '%s' of player %s, but it does not exist anymore".formatted(gunName, gamePlayer.getName()));
            return;
        }

        Gun gun = itemCreator.createGun(gunName, gamePlayer);
        gun.getResourceContainer().setLoadedAmount(gunState.magazineAmmo());
        gun.getResourceContainer().setReserveAmount(gunState.reserveAmmo());
        gun.update();

        gamePlayer.setItem(gunState.itemSlot(), gun.getItemStack());
    }

    private void loadEquipmentState(GamePlayer gamePlayer, EquipmentState equipmentState) {
        String equipmentName = equipmentState.equipmentName();

        if (itemSpecRegistry.getEquipmentSpec(equipmentName).isEmpty()) {
            logger.severe("Attempted to load equipment '%s' of player %s, but it does not exist anymore".formatted(equipmentName, gamePlayer.getName()));
            return;
        }

        Equipment equipment = itemCreator.createEquipment(equipmentName, gamePlayer);
        equipment.update();

        gamePlayer.setItem(equipmentState.itemSlot(), equipment.getItemStack());
    }

    private void loadMeleeWeaponState(GamePlayer gamePlayer, MeleeWeaponState meleeWeaponState) {
        String meleeWeaponName = meleeWeaponState.meleeWeaponName();

        if (itemSpecRegistry.getMeleeWeaponSpec(meleeWeaponName).isEmpty()) {
            logger.severe("Attempted to load melee weapon '%s' of player %s, but it does not exist anymore".formatted(meleeWeaponName, gamePlayer.getName()));
            return;
        }

        MeleeWeapon meleeWeapon = itemCreator.createMeleeWeapon(meleeWeaponName, gamePlayer);
        meleeWeapon.getResourceContainer().setLoadedAmount(meleeWeaponState.loadedAmount());
        meleeWeapon.getResourceContainer().setReserveAmount(meleeWeaponState.reserveAmount());
        meleeWeapon.update();

        gamePlayer.setItem(meleeWeaponState.itemSlot(), meleeWeapon.getItemStack());
    }


    public void savePlayerState(GamePlayer gamePlayer) {
        List<GunState> gunStates = gunRegistry.getAssignedGuns(gamePlayer).stream()
                .map(gun -> this.convertToGunState(gamePlayer, gun))
                .flatMap(Optional::stream)
                .toList();
        List<EquipmentState> equipmentStates = equipmentRegistry.getAssignedEquipmentList(gamePlayer).stream()
                .map(equipment -> this.convertToEquipmentState(gamePlayer, equipment))
                .flatMap(Optional::stream)
                .toList();
        List<MeleeWeaponState> meleeWeaponStates = meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer).stream()
                .map(meleeWeapon -> this.convertToMeleeWeaponState(gamePlayer, meleeWeapon))
                .flatMap(Optional::stream)
                .toList();

        UUID uniqueId = gamePlayer.getUniqueId();
        PlayerState playerState = new PlayerState(uniqueId, gunStates, equipmentStates, meleeWeaponStates);

        playerStateStorage.deletePlayerState(uniqueId);
        playerStateStorage.savePlayerState(playerState);
    }

    public void saveState() {
        logger.info("Saving current freeplay mode state");

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
        ItemStack itemStack = gun.getItemStack();

        if (itemStack == null) {
            return Optional.empty();
        }

        UUID uniqueId = gamePlayer.getUniqueId();
        String gunName = gun.getName();
        int magazineAmmo = gun.getResourceContainer().getLoadedAmount();
        int reserveAmmo = gun.getResourceContainer().getReserveAmount();

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(gun);
        return itemSlot.map(itemSlotValue -> new GunState(uniqueId, gunName, magazineAmmo, reserveAmmo, itemSlotValue));
    }

    private Optional<EquipmentState> convertToEquipmentState(GamePlayer gamePlayer, Equipment equipment) {
        ItemStack itemStack = equipment.getItemStack();

        if (itemStack == null) {
            return Optional.empty();
        }

        UUID uniqueId = gamePlayer.getUniqueId();
        String equipmentName = equipment.getName();

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(equipment);
        return itemSlot.map(integer -> new EquipmentState(uniqueId, equipmentName, integer));
    }

    private Optional<MeleeWeaponState> convertToMeleeWeaponState(GamePlayer gamePlayer, MeleeWeapon meleeWeapon) {
        ItemStack itemStack = meleeWeapon.getItemStack();

        if (itemStack == null) {
            return Optional.empty();
        }

        UUID uniqueId = gamePlayer.getUniqueId();
        String equipmentName = meleeWeapon.getName();
        int loadedAmount = meleeWeapon.getResourceContainer().getLoadedAmount();
        int reserveAmount = meleeWeapon.getResourceContainer().getReserveAmount();

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(meleeWeapon);
        return itemSlot.map(itemSlotValue -> new MeleeWeaponState(uniqueId, equipmentName, loadedAmount, reserveAmount, itemSlotValue));
    }
}
