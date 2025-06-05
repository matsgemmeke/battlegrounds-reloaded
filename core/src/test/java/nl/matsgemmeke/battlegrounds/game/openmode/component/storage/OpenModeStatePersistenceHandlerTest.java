package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.storage.state.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerState;
import nl.matsgemmeke.battlegrounds.storage.state.PlayerStateStorage;
import nl.matsgemmeke.battlegrounds.storage.state.StateStorageException;
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

    private GunRegistry gunRegistry;
    private Logger logger;
    private PlayerRegistry playerRegistry;
    private PlayerStateStorage playerStateStorage;
    private WeaponCreator weaponCreator;

    @BeforeEach
    public void setUp() {
        gunRegistry = mock(GunRegistry.class);
        logger = mock(Logger.class);
        playerRegistry = mock(PlayerRegistry.class);
        playerStateStorage = mock(PlayerStateStorage.class);
        weaponCreator = mock(WeaponCreator.class);
    }

    @Test
    public void loadStateAssignsItemsToGamePlayerBasedOnSavedState() {
        GunState gunState = new GunState(GUN_ID, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, GUN_ITEM_SLOT);
        PlayerState gamePlayerState = new PlayerState(PLAYER_UUID, List.of(gunState));
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(0, 0, 0, 0);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInventory inventory = mock(PlayerInventory.class);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getInventory()).thenReturn(inventory);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);

        Gun gun = mock(Gun.class);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(gun.getItemStack()).thenReturn(itemStack);

        when(playerStateStorage.findPlayerStateByPlayerUuid(PLAYER_UUID)).thenReturn(gamePlayerState);
        when(weaponCreator.createGun(GUN_ID, gamePlayer, GameKey.ofOpenMode())).thenReturn(gun);
        when(weaponCreator.gunExists(GUN_ID)).thenReturn(true);

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, gunRegistry, playerRegistry);
        statePersistenceHandler.loadState(gamePlayer);

        assertThat(ammunitionStorage.getMagazineAmmo()).isEqualTo(GUN_MAGAZINE_AMMO);
        assertThat(ammunitionStorage.getReserveAmmo()).isEqualTo(GUN_RESERVE_AMMO);

        verify(inventory).setItem(GUN_ITEM_SLOT, itemStack);
        verify(weaponCreator).createGun(GUN_ID, gamePlayer, GameKey.ofOpenMode());
    }

    @Test
    public void loadStateLogsErrorMessageWhenLoadingGunStateWhoseGunIdDoesNotExist() {
        GunState gunState = new GunState(GUN_ID, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, GUN_ITEM_SLOT);
        PlayerState playerState = new PlayerState(PLAYER_UUID, List.of(gunState));

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getName()).thenReturn("TestPlayer");

        when(playerStateStorage.findPlayerStateByPlayerUuid(PLAYER_UUID)).thenReturn(playerState);
        when(weaponCreator.gunExists(GUN_ID)).thenReturn(false);

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, gunRegistry, playerRegistry);
        statePersistenceHandler.loadState(gamePlayer);

        verify(logger).severe("Attempted to load gun 'TEST_GUN' from the open mode of player TestPlayer, but it does not exist anymore");
    }

    @Test
    public void saveStateSavesCollectedDataWithCorrespondingItemSlotsToStateStorage() throws StateStorageException {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(GUN_MAGAZINE_AMMO, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, Integer.MAX_VALUE);
        ItemStack gunItemStack = new ItemStack(Material.IRON_HOE);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getItemSlot(gunItemStack)).thenReturn(Optional.of(GUN_ITEM_SLOT));

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(gun.getItemStack()).thenReturn(gunItemStack);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(gun));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, gunRegistry, playerRegistry);
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
    }

    @Test
    public void saveStateSavesCollectedDataWithoutItemsWithoutItemStack() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(GUN_MAGAZINE_AMMO, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, Integer.MAX_VALUE);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getName()).thenReturn("TestPlayer");

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(gun.getItemStack()).thenReturn(null);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(gun));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, gunRegistry, playerRegistry);
        statePersistenceHandler.saveState();

        ArgumentCaptor<PlayerState> playerStateCaptor = ArgumentCaptor.forClass(PlayerState.class);
        verify(playerStateStorage).savePlayerState(playerStateCaptor.capture());

        PlayerState savedPlayerState = playerStateCaptor.getValue();

        assertThat(savedPlayerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(savedPlayerState.gunStates()).isEmpty();

        verify(logger).severe("Cannot save state for gun TEST_GUN of player TestPlayer, since it has no item stack");
    }

    @Test
    public void saveStateSavesCollectedDataWithoutItemsWhoseItemSlotCannotBeDetermined() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(GUN_MAGAZINE_AMMO, GUN_MAGAZINE_AMMO, GUN_RESERVE_AMMO, Integer.MAX_VALUE);
        ItemStack gunItemStack = new ItemStack(Material.IRON_HOE);

        GamePlayer gamePlayer = mock(GamePlayer.class, RETURNS_DEEP_STUBS);
        when(gamePlayer.getEntity().getUniqueId()).thenReturn(PLAYER_UUID);
        when(gamePlayer.getItemSlot(gunItemStack)).thenReturn(Optional.empty());
        when(gamePlayer.getName()).thenReturn("TestPlayer");

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(gun.getItemStack()).thenReturn(gunItemStack);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(gunRegistry.getAssignedItems(gamePlayer)).thenReturn(List.of(gun));

        OpenModeStatePersistenceHandler statePersistenceHandler = new OpenModeStatePersistenceHandler(logger, playerStateStorage, weaponCreator, gunRegistry, playerRegistry);
        statePersistenceHandler.saveState();

        ArgumentCaptor<PlayerState> playerStateCaptor = ArgumentCaptor.forClass(PlayerState.class);
        verify(playerStateStorage).savePlayerState(playerStateCaptor.capture());

        PlayerState savedPlayerState = playerStateCaptor.getValue();

        assertThat(savedPlayerState.playerUuid()).isEqualTo(PLAYER_UUID);
        assertThat(savedPlayerState.gunStates()).isEmpty();

        verify(logger).severe("Cannot save state for gun TEST_GUN of player TestPlayer, since its item slot cannot be determined");
    }
}
