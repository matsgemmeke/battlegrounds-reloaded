package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
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
    private ItemControls<MeleeWeaponHolder> controls;
    @Mock
    private MeleeWeaponHolder holder;
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
    void onChangeFromDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onChangeFrom();

        verifyNoInteractions(controls);
    }

    @Test
    void onChangeFromPerformsChangeFromActionOnControls() {
        meleeWeapon.setControls(controls);
        meleeWeapon.setHolder(holder);
        meleeWeapon.onChangeFrom();

        verify(controls).performAction(Action.CHANGE_FROM, holder);
    }

    @Test
    void onChangeToDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onChangeTo();

        verifyNoInteractions(controls);
    }

    @Test
    void onChangeToPerformsChangeToActionOnControls() {
        meleeWeapon.setControls(controls);
        meleeWeapon.setHolder(holder);
        meleeWeapon.onChangeTo();

        verify(controls).performAction(Action.CHANGE_TO, holder);
    }

    @Test
    void onDropDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onDrop();

        verifyNoInteractions(controls);
    }

    @Test
    void onDropPerformsDropActionOnControlsAndCancelsOtherFunctions() {
        meleeWeapon.setControls(controls);
        meleeWeapon.setHolder(holder);
        meleeWeapon.onDrop();

        assertThat(meleeWeapon.getHolder()).isEmpty();

        verify(controls).cancelAllFunctions();
        verify(controls).performAction(Action.DROP_ITEM, holder);
    }

    @Test
    void onLeftClickDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onLeftClick();

        verifyNoInteractions(controls);
    }

    @Test
    void onLeftClickPerformsLeftClickActionOnControls() {
        meleeWeapon.setControls(controls);
        meleeWeapon.setHolder(holder);
        meleeWeapon.onLeftClick();

        verify(controls).performAction(Action.LEFT_CLICK, holder);
    }

    @Test
    void onPickupPerformsPickupActionOnControlsAndSetsHolder() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onPickUp(holder);

        assertThat(meleeWeapon.getHolder()).hasValue(holder);

        verify(controls).performAction(Action.PICKUP_ITEM, holder);
    }

    @Test
    void onRightClickDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onRightClick();

        verifyNoInteractions(controls);
    }

    @Test
    void onRightClickPerformsRightClickActionOnControls() {
        meleeWeapon.setControls(controls);
        meleeWeapon.setHolder(holder);
        meleeWeapon.onRightClick();

        verify(controls).performAction(Action.RIGHT_CLICK, holder);
    }

    @Test
    void onSwapFromDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onSwapFrom();

        verifyNoInteractions(controls);
    }

    @Test
    void onSwapFromPerformsSwapFromActionOnControls() {
        meleeWeapon.setControls(controls);
        meleeWeapon.setHolder(holder);
        meleeWeapon.onSwapFrom();

        verify(controls).performAction(Action.SWAP_FROM, holder);
    }

    @Test
    void onSwapToDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.setControls(controls);
        meleeWeapon.onSwapTo();

        verifyNoInteractions(controls);
    }

    @Test
    void onSwapToPerformsSwapToActionOnControls() {
        meleeWeapon.setControls(controls);
        meleeWeapon.setHolder(holder);
        meleeWeapon.onSwapTo();

        verify(controls).performAction(Action.SWAP_TO, holder);
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
        assertThatCode(() -> meleeWeapon.performThrow(holder)).doesNotThrowAnyException();
    }

    @Test
    void performThrowDelegatesToThrowHandler() {
        ThrowHandler throwHandler = mock(ThrowHandler.class);

        meleeWeapon.configureThrowHandler(throwHandler);
        meleeWeapon.performThrow(holder);

        verify(throwHandler).performThrow(holder);
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
