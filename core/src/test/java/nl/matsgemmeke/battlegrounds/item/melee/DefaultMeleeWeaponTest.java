package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandler;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultMeleeWeaponTest {

    private static final String NAME = "Combat Knife";

    @Mock
    private ItemController<MeleeWeaponUser> controller;
    @Mock
    private MeleeWeaponUser user;
    @Mock
    private ReloadSystem reloadSystem;
    @Captor
    private ArgumentCaptor<Map<String, Object>> templateValuesCaptor;

    private DefaultMeleeWeapon meleeWeapon;

    @BeforeEach
    void setUp() {
        meleeWeapon = new DefaultMeleeWeapon();
    }

    @Test
    @DisplayName("assign sets the user")
    void assign_setUser() {
        meleeWeapon.assign(user);

        assertThat(meleeWeapon.getUser()).hasValue(user);
    }

    @Test
    @DisplayName("unassign sets the user to null")
    void unassign_removesUser() {
        meleeWeapon.assign(user);
        meleeWeapon.unassign();

        assertThat(meleeWeapon.getUser()).isEmpty();
    }

    @Test
    void isMatchingReturnsFalseWhenDisplayItemTemplateIsNull() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        boolean matching = meleeWeapon.isMatching(itemStack);

        assertThat(matching).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    void isMatchingReturnsWhetherDisplayItemTemplateMatchesGivenItemStack(boolean matchesItemStack, boolean expectedResult) {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.matchesTemplate(itemStack)).thenReturn(matchesItemStack);

        meleeWeapon.setDisplayItemTemplate(displayItemTemplate);
        boolean matching = meleeWeapon.isMatching(itemStack);

        assertThat(matching).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("onChangeFrom does not perform action when no user is assigned")
    void onChangeFrom_nullUser() {
        meleeWeapon.setController(controller);
        meleeWeapon.onChangeFrom();

        verifyNoInteractions(controller);
    }

    @Test
    @DisplayName("onChangeFrom performs CHANGE_FROM action on controller when user is assigned")
    void onChangeFrom_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.assign(user);
        meleeWeapon.onChangeFrom();

        verify(controller).performAction(Action.CHANGE_FROM, user);
    }

    @Test
    @DisplayName("onChangeTo does not perform action when no user is assigned")
    void onChangeTo_nullUser() {
        meleeWeapon.setController(controller);
        meleeWeapon.onChangeTo();

        verifyNoInteractions(controller);
    }

    @Test
    @DisplayName("onChangeTo performs CHANGE_TO action on controller when user is assigned")
    void onChangeTo_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.assign(user);
        meleeWeapon.onChangeTo();

        verify(controller).performAction(Action.CHANGE_TO, user);
    }

    @Test
    @DisplayName("onDrop does not perform action when no user is assigned")
    void onDrop_nullUser() {
        meleeWeapon.setController(controller);
        meleeWeapon.onDrop();

        verifyNoInteractions(controller);
    }

    @Test
    @DisplayName("onDrop performs DROP_ITEM action on controller and cancels other functions when user is assigned")
    void onDrop_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.assign(user);
        meleeWeapon.onDrop();

        verify(controller).cancelAllFunctions();
        verify(controller).performAction(Action.DROP_ITEM, user);
    }

    @Test
    @DisplayName("onLeftClick does not perform action when no user is assigned")
    void onLeftClick_nullUser() {
        meleeWeapon.setController(controller);
        meleeWeapon.onLeftClick();

        verifyNoInteractions(controller);
    }

    @Test
    @DisplayName("onLeftClick performs LEFT_CLICK action on controller when user is assigned")
    void onLeftClick_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.assign(user);
        meleeWeapon.onLeftClick();

        verify(controller).performAction(Action.LEFT_CLICK, user);
    }

    @Test
    @DisplayName("onPickUp performs PICKUP_ITEM action on controller and assigns user")
    void onPickup_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.onPickUp(user);

        verify(controller).performAction(Action.PICKUP_ITEM, user);
    }

    @Test
    @DisplayName("onRightClick does not perform action when no user is assigned")
    void onRightClick_nullUser() {
        meleeWeapon.setController(controller);
        meleeWeapon.onRightClick();

        verifyNoInteractions(controller);
    }

    @Test
    @DisplayName("onRightClick performs RIGHT_CLICK action on controller when user is assigned")
    void onRightClick_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.assign(user);
        meleeWeapon.onRightClick();

        verify(controller).performAction(Action.RIGHT_CLICK, user);
    }

    @Test
    @DisplayName("onSwapFrom does not perform action when no user is assigned")
    void onSwapFrom_nullUser() {
        meleeWeapon.setController(controller);
        meleeWeapon.onSwapFrom();

        verifyNoInteractions(controller);
    }

    @Test
    @DisplayName("onSwapFrom performs SWAP_FROM action on controller when user is assigned")
    void onSwapFrom_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.assign(user);
        meleeWeapon.onSwapFrom();

        verify(controller).performAction(Action.SWAP_FROM, user);
    }

    @Test
    @DisplayName("onSwapTo does not perform action when no user is assigned")
    void onSwapTo_nullUser() {
        meleeWeapon.setController(controller);
        meleeWeapon.onSwapTo();

        verifyNoInteractions(controller);
    }

    @Test
    @DisplayName("onSwapTo performs SWAP_TO action on controller when user is assigned")
    void onSwapTo_success() {
        meleeWeapon.setController(controller);
        meleeWeapon.assign(user);
        meleeWeapon.onSwapTo();

        verify(controller).performAction(Action.SWAP_TO, user);
    }

    @Test
    @DisplayName("cancelReload cancels reload system")
    void cancelReload_cancelsReload() {
        meleeWeapon.setReloadSystem(reloadSystem);
        meleeWeapon.cancelReload();

        verify(reloadSystem).cancelReload();
    }

    @Test
    @DisplayName("isReloadAvailable returns false when reload system is already performing")
    void isReloadAvailable_whenReloading() {
        when(reloadSystem.isPerforming()).thenReturn(true);

        meleeWeapon.setReloadSystem(reloadSystem);
        boolean reloadAvailable = meleeWeapon.isReloadAvailable();

        assertThat(reloadAvailable).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "1,0", "0,0" })
    @DisplayName("isReloadAvailable returns false when ResourceContainer does not contain sufficient resources")
    void isReloadAvailable_whenCapacityIsFull(int loadedAmount, int reserveAmount) {
        ResourceContainer resourceContainer = new ResourceContainer(1, loadedAmount, reserveAmount, 0);

        when(reloadSystem.isPerforming()).thenReturn(false);

        meleeWeapon.setReloadSystem(reloadSystem);
        meleeWeapon.setResourceContainer(resourceContainer);
        boolean reloadAvailable = meleeWeapon.isReloadAvailable();

        assertThat(reloadAvailable).isFalse();
    }

    @Test
    @DisplayName("isReloadAvailable returns true when not reloading and ResourceContainer has sufficient resources")
    void isReloadAvailable_notReloadingAndSufficientResources() {
        ResourceContainer resourceContainer = new ResourceContainer(1, 0, 1, 1);

        when(reloadSystem.isPerforming()).thenReturn(false);

        meleeWeapon.setReloadSystem(reloadSystem);
        meleeWeapon.setResourceContainer(resourceContainer);
        boolean reloadAvailable = meleeWeapon.isReloadAvailable();

        assertThat(reloadAvailable).isTrue();
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isReloading returns whether reload system is reloading")
    void isReloading_returnsReloadSystemReloadingState(boolean reloadSystemReload, boolean expectedResult) {
        when(reloadSystem.isPerforming()).thenReturn(reloadSystemReload);

        meleeWeapon.setReloadSystem(reloadSystem);
        boolean reloading = meleeWeapon.isReloading();

        assertThat(reloading).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("reload start reload system performance that updates the item stack")
    void reload_performsReload() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(itemStack);

        doAnswer(MockUtils.answerApplyReloadProcedure()).when(reloadSystem).performReload(eq(performer), any(Procedure.class));

        meleeWeapon.setDisplayItemTemplate(displayItemTemplate);
        meleeWeapon.setReloadSystem(reloadSystem);
        meleeWeapon.reload(performer);

        verify(performer).setHeldItem(itemStack);
    }

    @Test
    void performThrowDoesNothingWhenThrowHandlerIsNull() {
        assertThatCode(() -> meleeWeapon.performThrow(user)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("performThrow deletes to ThrowHandler and unassigns user when no more resources are left")
    void performThrow_delegatesToThrowHandler() {
        ResourceContainer resourceContainer = new ResourceContainer(1, 0, 0, 1);
        ThrowHandler throwHandler = mock(ThrowHandler.class);

        meleeWeapon.setResourceContainer(resourceContainer);
        meleeWeapon.configureThrowHandler(throwHandler);
        meleeWeapon.assign(user);
        meleeWeapon.performThrow(user);

        assertThat(meleeWeapon.getUser()).isEmpty();

        verify(throwHandler).performThrow(user);
    }

    @Test
    void updateReturnsFalseWhenDisplayItemTemplateIsNull() {
        boolean updated = meleeWeapon.update();

        assertThat(updated).isFalse();
    }

    @Test
    void updateReturnsTrueAndCreatesNewItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 2);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(itemStack);

        meleeWeapon.setName(NAME);
        meleeWeapon.setDisplayItemTemplate(displayItemTemplate);
        meleeWeapon.setResourceContainer(resourceContainer);
        boolean updated = meleeWeapon.update();

        assertThat(updated).isTrue();

        verify(displayItemTemplate).createItemStack(templateValuesCaptor.capture());

        assertThat(templateValuesCaptor.getValue()).isEqualTo(Map.of(
                "name", NAME,
                "loaded_amount", 1,
                "reserve_amount", 2
        ));
    }
}
