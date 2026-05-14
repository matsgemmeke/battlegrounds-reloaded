package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.PickupDispatchResult;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemInteractionDispatcherTest {

    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private ItemInteractionHandler itemInteractionHandler;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ItemStack itemStack;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @InjectMocks
    private ItemInteractionDispatcher itemInteractionDispatcher;

    @BeforeEach
    void setUp() {
        itemInteractionDispatcher.registerInteractionHandler(WeaponType.GUN, itemInteractionHandler);
    }

    @Test
    @DisplayName("dispatchChangeFrom returns unhandled result when given item stack has no item meta")
    void dispatchChangeFrom_itemStackWithoutItemMeta() {
        when(itemStack.getItemMeta()).thenReturn(null);

        DispatchResult result = itemInteractionDispatcher.dispatchChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatchChangeFrom returns unhandled result when given item stack has no weapon type data value")
    void dispatchChangeFrom_itemStackWithoutWeaponTypeData() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn(null);

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);

        DispatchResult result = itemInteractionDispatcher.dispatchChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatchChangeFrom returns unhandled result when given item stack has no interaction handler for the weapon type")
    void dispatchChangeFrom_itemStackWithoutInteractionHandler() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("EQUIPMENT");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);

        DispatchResult result = itemInteractionDispatcher.dispatchChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatchChangeFrom returns unhandled result when selected interaction handler did not handle the interaction")
    void dispatchChangeFrom_interactionHandlerUnhandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleChangeFrom(gamePlayer, itemStack)).thenReturn(DispatchResult.unhandled());

        DispatchResult result = itemInteractionDispatcher.dispatchChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatchChangeFrom returns handled result from interaction handler")
    void dispatchChangeFrom_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleChangeFrom(gamePlayer, itemStack)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatchChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("dispatchChangeTo returns handled result from interaction handler")
    void dispatchChangeTo_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleChangeTo(gamePlayer, itemStack)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatchChangeTo(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("dispatchDropItem returns handled result from interaction handler")
    void dispatchDropItem_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleDropItem(gamePlayer, itemStack)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatchDropItem(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("dispatchLeftClick returns handled result from interaction handler")
    void dispatchLeftClick_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleLeftClick(gamePlayer, itemStack)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatchLeftClick(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("dispatchPickupItem returns handled result from interaction handler")
    void dispatchPickupItem_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        PickupDispatchResult handlerResult = new PickupDispatchResult(true, true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handlePickupItem(gamePlayer, itemStack)).thenReturn(handlerResult);

        PickupDispatchResult result = itemInteractionDispatcher.dispatchPickupItem(gamePlayer, itemStack);

        assertThat(result).isEqualTo(handlerResult);
    }

    @Test
    @DisplayName("dispatchRightClick returns handled result from interaction handler")
    void dispatchRightClick_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleRightClick(gamePlayer, itemStack)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatchRightClick(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("dispatchSwapFrom returns handled result from interaction handler")
    void dispatchSwapFrom_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleSwapFrom(gamePlayer, itemStack)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatchSwapFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("dispatchSwapTo returns handled result from interaction handler")
    void dispatchSwapTo_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleSwapTo(gamePlayer, itemStack)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatchSwapTo(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
