package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemInteractionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
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
        itemInteractionDispatcher.registerInteractionHandler(Action.LEFT_CLICK, WeaponType.GUN, itemInteractionHandler);
    }

    @Test
    @DisplayName("dispatch returns unhandled result when given item stack has no item meta")
    void dispatch_itemStackWithoutItemMeta() {
        when(itemStack.getItemMeta()).thenReturn(null);

        DispatchResult result = itemInteractionDispatcher.dispatch(gamePlayer, itemStack, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatch returns unhandled result when given item stack has no weapon type data value")
    void dispatch_itemStackWithoutWeaponTypeData() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn(null);

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);

        DispatchResult result = itemInteractionDispatcher.dispatch(gamePlayer, itemStack, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatch returns unhandled result when given item stack has no interaction handler for the weapon type")
    void dispatch_itemStackWithoutInteractionHandler() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("EQUIPMENT");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);

        DispatchResult result = itemInteractionDispatcher.dispatch(gamePlayer, itemStack, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatch returns handled result from interaction handler")
    void dispatch_interactionHandlerHandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");
        DispatchResult handlerResult = new DispatchResult(true, true);

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleInteraction(gamePlayer, itemStack, Action.LEFT_CLICK)).thenReturn(handlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatch(gamePlayer, itemStack, Action.LEFT_CLICK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("dispatch returns unhandled result when selected interaction handler did not handle the interaction")
    void dispatch_interactionHandlerUnhandled() {
        NamespacedKey key = NamespacedKey.fromString("weapon-type");

        PersistentDataContainer data = mock(PersistentDataContainer.class);
        when(data.get(key, PersistentDataType.STRING)).thenReturn("GUN");

        when(itemStack.getItemMeta().getPersistentDataContainer()).thenReturn(data);
        when(namespacedKeyCreator.create("weapon-type")).thenReturn(key);
        when(itemInteractionHandler.handleInteraction(gamePlayer, itemStack, Action.LEFT_CLICK)).thenReturn(DispatchResult.unhandled());

        DispatchResult result = itemInteractionDispatcher.dispatch(gamePlayer, itemStack, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }
}
