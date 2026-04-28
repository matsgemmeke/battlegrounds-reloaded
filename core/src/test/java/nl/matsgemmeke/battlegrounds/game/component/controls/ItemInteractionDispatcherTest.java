package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemInteractionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemInteractionDispatcherTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);

    @Mock
    private ItemInteractionHandler<Gun> gunInteractionHandler;
    @Mock
    private GamePlayer gamePlayer;
    @InjectMocks
    private ItemInteractionDispatcher itemInteractionDispatcher;

    @BeforeEach
    void setUp() {
        itemInteractionDispatcher.registerActionHandler(Action.LEFT_CLICK, gunInteractionHandler);
    }

    @ParameterizedTest
    @CsvSource({
            "true,true,true,true",
            "false,false,false,false"
    })
    @DisplayName("dispatch returns dispatch result from underlying action handler when one resolves the given player and item stack")
    void dispatch_actionHandlerResolvesGivenPlayerAndItemStack(
            boolean actionHandlerHandled,
            boolean actionHandlerCancelEvent,
            boolean expectedHandled,
            boolean expectedCancelEvent
    ) {
        Gun gun = mock(Gun.class);
        DispatchResult actionHandlerResult = new DispatchResult(actionHandlerHandled, actionHandlerCancelEvent);

        when(gunInteractionHandler.resolve(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gunInteractionHandler.dispatch(gun, gamePlayer, Action.LEFT_CLICK)).thenReturn(actionHandlerResult);

        DispatchResult result = itemInteractionDispatcher.dispatch(gamePlayer, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result.handled()).isEqualTo(expectedHandled);
        assertThat(result.cancelEvent()).isEqualTo(expectedCancelEvent);
    }

    @Test
    @DisplayName("dispatch returns unhandled dispatch result when none of the action handlers resolves the given player and item stack")
    void dispatch_noActionHandlerResolvesGivenPlayerAndItemStack() {
        when(gunInteractionHandler.resolve(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        DispatchResult result = itemInteractionDispatcher.dispatch(gamePlayer, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }
}
