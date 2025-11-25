package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultMeleeWeaponTest {

    private static final String NAME = "Combat Knife";

    @Mock
    private ItemControls<MeleeWeaponHolder> controls;
    @Mock
    private MeleeWeaponHolder holder;
    @InjectMocks
    private DefaultMeleeWeapon meleeWeapon;
    @Captor
    private ArgumentCaptor<Map<String, Object>> templateValuesCaptor;

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
        meleeWeapon.onChangeFrom();

        verifyNoInteractions(controls);
    }

    @Test
    void onChangeFromPerformsChangeFromActionOnControls() {
        meleeWeapon.setHolder(holder);
        meleeWeapon.onChangeFrom();

        verify(controls).performAction(Action.CHANGE_FROM, holder);
    }

    @Test
    void onChangeToDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.onChangeTo();

        verifyNoInteractions(controls);
    }

    @Test
    void onChangeToPerformsChangeToActionOnControls() {
        meleeWeapon.setHolder(holder);
        meleeWeapon.onChangeTo();

        verify(controls).performAction(Action.CHANGE_TO, holder);
    }

    @Test
    void onDropDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.onDrop();

        verifyNoInteractions(controls);
    }

    @Test
    void onDropPerformsDropActionOnControlsAndCancelsOtherFunctions() {
        meleeWeapon.setHolder(holder);
        meleeWeapon.onDrop();

        assertThat(meleeWeapon.getHolder()).isEmpty();

        verify(controls).cancelAllFunctions();
        verify(controls).performAction(Action.DROP_ITEM, holder);
    }

    @Test
    void onLeftClickDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.onLeftClick();

        verifyNoInteractions(controls);
    }

    @Test
    void onLeftClickPerformsLeftClickActionOnControls() {
        meleeWeapon.setHolder(holder);
        meleeWeapon.onLeftClick();

        verify(controls).performAction(Action.LEFT_CLICK, holder);
    }

    @Test
    void onPickupPerformsPickupActionOnControlsAndSetsHolder() {
        meleeWeapon.onPickUp(holder);

        assertThat(meleeWeapon.getHolder()).hasValue(holder);

        verify(controls).performAction(Action.PICKUP_ITEM, holder);
    }

    @Test
    void onRightClickDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.onRightClick();

        verifyNoInteractions(controls);
    }

    @Test
    void onRightClickPerformsRightClickActionOnControls() {
        meleeWeapon.setHolder(holder);
        meleeWeapon.onRightClick();

        verify(controls).performAction(Action.RIGHT_CLICK, holder);
    }

    @Test
    void onSwapFromDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.onSwapFrom();

        verifyNoInteractions(controls);
    }

    @Test
    void onSwapFromPerformsSwapFromActionOnControls() {
        meleeWeapon.setHolder(holder);
        meleeWeapon.onSwapFrom();

        verify(controls).performAction(Action.SWAP_FROM, holder);
    }

    @Test
    void onSwapToDoesNotPerformActionWhenHolderIsNull() {
        meleeWeapon.onSwapTo();

        verifyNoInteractions(controls);
    }

    @Test
    void onSwapToPerformsSwapToActionOnControls() {
        meleeWeapon.setHolder(holder);
        meleeWeapon.onSwapTo();

        verify(controls).performAction(Action.SWAP_TO, holder);
    }

    @Test
    void updateReturnsFalseWhenDisplayItemTemplateIsNull() {
        boolean updated = meleeWeapon.update();

        assertThat(updated).isFalse();
    }

    @Test
    void updateReturnsTrueAndCreatesNewItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(itemStack);

        meleeWeapon.setName(NAME);
        meleeWeapon.setDisplayItemTemplate(displayItemTemplate);
        boolean updated = meleeWeapon.update();

        assertThat(updated).isTrue();

        verify(displayItemTemplate).createItemStack(templateValuesCaptor.capture());

        assertThat(templateValuesCaptor.getValue()).satisfies(values -> {
            assertThat(values.get("name")).isEqualTo(NAME);
        });
    }
}
