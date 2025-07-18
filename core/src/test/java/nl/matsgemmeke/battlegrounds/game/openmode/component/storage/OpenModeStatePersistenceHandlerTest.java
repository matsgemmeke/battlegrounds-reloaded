package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorageException;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OpenModeStatePersistenceHandlerTest {

    private static final UUID PLAYER_UUID = UUID.randomUUID();
    private static final String GUN_ID = "TEST_GUN";
    private static final int GUN_MAGAZINE_AMMO = 10;
    private static final int GUN_RESERVE_AMMO = 20;
    private static final int GUN_ITEM_SLOT = 5;
    private static final String EQUIPMENT_ID = "TEST_EQUIPMENT";
    private static final int EQUIPMENT_ITEM_SLOT = 6;

    private EquipmentRegistry equipmentRegistry;
    private GunRegistry gunRegistry;
    private Logger logger;
    private PlayerRegistry playerRegistry;
    private PlayerStateStorage playerStateStorage;
    private WeaponCreator weaponCreator;

    @BeforeEach
    public void setUp() {
        equipmentRegistry = mock(EquipmentRegistry.class);
        gunRegistry = mock(GunRegistry.class);
        logger = mock(Logger.class);
        playerRegistry = mock(PlayerRegistry.class);
        playerStateStorage = mock(PlayerStateStorage.class);
        weaponCreator = mock(WeaponCreator.class);
    }

    @Test
    public void loadPlayerStateAssignsItemsToGamePlayerBasedOnSavedState() {
        GunState gunState = new GunState(PLAYER_UUID, GUN_ID, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, GUN_ITEM_SLOT);
        EquipmentState equipmentState = new EquipmentState(PLAYER_UUID, EQUIPMENT_ID, EQUIPMENT_ITEM_SLOT);
        PlayerState gamePlayerState = new PlayerState(PLAYER_UUID, List.of(gunState), List.of(equipmentState));
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(0, 0, 0, 0);
        ItemStack gunItemStack = new ItemStack(Material.IRON_HOE);
        ItemStack equipmentItemStack = new ItemStack(Material.SHEARS);
        PlayerInventory inventory = mock(PlayerInventory.class);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getInventory()).thenReturn(inventory);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);

        Gun gun = mock(Gun.class);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(gun.getItemStack()).thenReturn(gunItemStack);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getItemStack()).thenReturn(equipmentItemStack);

        when(playerStateStorage.getPlayerState(PLAYER_UUID)).thenReturn(gamePlayerState);
        when(weaponCreator.createGun(GUN_ID, gamePlayer, GameKey.ofOpenMode())).thenReturn(gun);
        when(weaponCreator.createEquipment(EQUIPMENT_ID, gamePlayer, GameKey.ofOpenMode())).thenReturn(equipment);
        when(weaponCreator.gunExists(GUN_ID)).thenReturn(true);
        when(weaponCreator.equipmentExists(EQUIPMENT_ID)).thenReturn(true);

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, equipmentRegistry, gunRegistry, playerRegistry);
        statePersistenceHandler.loadPlayerState(gamePlayer);

        assertThat(ammunitionStorage.getMagazineAmmo()).isEqualTo(GUN_MAGAZINE_AMMO);
        assertThat(ammunitionStorage.getReserveAmmo()).isEqualTo(GUN_RESERVE_AMMO);

        verify(inventory).setItem(GUN_ITEM_SLOT, gunItemStack);
        verify(inventory).setItem(EQUIPMENT_ITEM_SLOT, equipmentItemStack);
        verify(weaponCreator).createGun(GUN_ID, gamePlayer, GameKey.ofOpenMode());
        verify(weaponCreator).createEquipment(EQUIPMENT_ID, gamePlayer, GameKey.ofOpenMode());
    }

    @Test
    public void loadPlayerStateLogsErrorMessageWhenLoadingGunStateWhoseGunIdDoesNotExist() {
        GunState gunState = new GunState(PLAYER_UUID, GUN_ID, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, GUN_ITEM_SLOT);
        PlayerState playerState = new PlayerState(PLAYER_UUID, List.of(gunState), List.of());

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getName()).thenReturn("TestPlayer");

        when(playerStateStorage.getPlayerState(PLAYER_UUID)).thenReturn(playerState);
        when(weaponCreator.gunExists(GUN_ID)).thenReturn(false);

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, equipmentRegistry, gunRegistry, playerRegistry);
        statePersistenceHandler.loadPlayerState(gamePlayer);

        verify(logger).severe("Attempted to load gun 'TEST_GUN' from the open mode of player TestPlayer, but it does not exist anymore");
    }

    @Test
    public void loadPlayerStateLogsErrorMessageWhenLoadingEquipmentStateWhoseEquipmentIdDoesNotExist() {
        EquipmentState equipmentState = new EquipmentState(PLAYER_UUID, EQUIPMENT_ID, EQUIPMENT_ITEM_SLOT);
        PlayerState playerState = new PlayerState(PLAYER_UUID, List.of(), List.of(equipmentState));

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getName()).thenReturn("TestPlayer");

        when(playerStateStorage.getPlayerState(PLAYER_UUID)).thenReturn(playerState);
        when(weaponCreator.equipmentExists(EQUIPMENT_ID)).thenReturn(false);

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, equipmentRegistry, gunRegistry, playerRegistry);
        statePersistenceHandler.loadPlayerState(gamePlayer);

        verify(logger).severe("Attempted to load equipment 'TEST_EQUIPMENT' from the open mode of player TestPlayer, but it does not exist anymore");
    }

    @Test
    public void savePlayerStateLogsErrorWhenSavingCollectedDataWithItemsWithoutItemStack() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(GUN_MAGAZINE_AMMO, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, Integer.MAX_VALUE);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getName()).thenReturn("TestPlayer");

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(gun.getItemStack()).thenReturn(null);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(equipment.getItemStack()).thenReturn(null);

        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(gun));
        when(equipmentRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(equipment));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, equipmentRegistry, gunRegistry, playerRegistry);
        statePersistenceHandler.savePlayerState(gamePlayer);

        ArgumentCaptor<PlayerState> playerStateCaptor = ArgumentCaptor.forClass(PlayerState.class);
        verify(playerStateStorage).savePlayerState(playerStateCaptor.capture());

        PlayerState savedPlayerState = playerStateCaptor.getValue();

        assertThat(savedPlayerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(savedPlayerState.gunStates()).isEmpty();
        assertThat(savedPlayerState.equipmentStates()).isEmpty();
    }

    @Test
    public void savePlayerStateLogsErrorWhenSavingCollectedDataWithItemsWhoseItemSlotCannotBeDetermined() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(GUN_MAGAZINE_AMMO, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, Integer.MAX_VALUE);

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getItemSlot(gun)).thenReturn(Optional.empty());
        when(gamePlayer.getItemSlot(equipment)).thenReturn(Optional.empty());
        when(gamePlayer.getName()).thenReturn("TestPlayer");

        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(gun));
        when(equipmentRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(equipment));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, equipmentRegistry, gunRegistry, playerRegistry);
        statePersistenceHandler.savePlayerState(gamePlayer);

        ArgumentCaptor<PlayerState> playerStateCaptor = ArgumentCaptor.forClass(PlayerState.class);
        verify(playerStateStorage).savePlayerState(playerStateCaptor.capture());

        PlayerState savedPlayerState = playerStateCaptor.getValue();

        assertThat(savedPlayerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(savedPlayerState.gunStates()).isEmpty();
        assertThat(savedPlayerState.equipmentStates()).isEmpty();
    }

    @Test
    public void saveStateSavesCollectedDataWithCorrespondingItemSlotsToStateStorage() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(GUN_MAGAZINE_AMMO, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, Integer.MAX_VALUE);
        ItemStack gunItemStack = new ItemStack(Material.IRON_HOE);
        ItemStack equipmentItemStack = new ItemStack(Material.SHEARS);

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(gun.getItemStack()).thenReturn(gunItemStack);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(equipment.getItemStack()).thenReturn(equipmentItemStack);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getItemSlot(gun)).thenReturn(Optional.of(GUN_ITEM_SLOT));
        when(gamePlayer.getItemSlot(equipment)).thenReturn(Optional.of(EQUIPMENT_ITEM_SLOT));

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(gun));
        when(equipmentRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(equipment));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, equipmentRegistry, gunRegistry, playerRegistry);
        statePersistenceHandler.saveState();

        ArgumentCaptor<PlayerState> playerStateCaptor = ArgumentCaptor.forClass(PlayerState.class);
        verify(playerStateStorage).savePlayerState(playerStateCaptor.capture());

        PlayerState savedPlayerState = playerStateCaptor.getValue();

        assertThat(savedPlayerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(savedPlayerState.gunStates()).hasSize(1);
        assertThat(savedPlayerState.gunStates().get(0).gunId()).isEqualTo(GUN_ID);
        assertThat(savedPlayerState.gunStates().get(0).magazineAmmo()).isEqualTo(GUN_MAGAZINE_AMMO);
        assertThat(savedPlayerState.gunStates().get(0).reserveAmmo()).isEqualTo(GUN_RESERVE_AMMO);
        assertThat(savedPlayerState.gunStates().get(0).itemSlot()).isEqualTo(GUN_ITEM_SLOT);
        assertThat(savedPlayerState.equipmentStates()).hasSize(1);
        assertThat(savedPlayerState.equipmentStates().get(0).equipmentId()).isEqualTo(EQUIPMENT_ID);
        assertThat(savedPlayerState.equipmentStates().get(0).itemSlot()).isEqualTo(EQUIPMENT_ITEM_SLOT);
    }

    @Test
    public void saveStateLogsErrorMessageWhenFailingToSaveAnyPlayerState() {
        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);

        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of());
        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        doThrow(new PlayerStateStorageException("error")).when(playerStateStorage).savePlayerState(any(PlayerState.class));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, equipmentRegistry, gunRegistry, playerRegistry);
        statePersistenceHandler.saveState();

        verify(logger).severe("Failed to save current state of 1 player(s). Caused by: error");
    }
}
