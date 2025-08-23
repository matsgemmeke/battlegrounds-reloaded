package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerInteractEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Player player;
    private Provider<ActionHandler> actionHandlerProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        actionHandlerProvider = mock();

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
    }

    @Test
    public void handleShouldDoNothingIfClickedItemIsNull() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, null, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameContextProvider, gameScope, actionHandlerProvider);
        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DEFAULT);

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, ITEM_STACK, null, null);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameContextProvider, gameScope, actionHandlerProvider);
        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DEFAULT);

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenGameKeyOfPlayerHasNoCorrespondingGameContext() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, ITEM_STACK, null, null);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameContextProvider, gameScope, actionHandlerProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerInteractEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    public void handleShouldCallLeftClickFunctionAndCancelEventBasedOnResult() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, ITEM_STACK, null, null);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemLeftClick(player, ITEM_STACK)).thenReturn(false);

        when(actionHandlerProvider.get()).thenReturn(actionHandler);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameContextProvider, gameScope, actionHandlerProvider);
        eventHandler.handle(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(GAME_CONTEXT), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        assertThat(event.useItemInHand()).isEqualTo(Result.DENY);

        verify(actionHandler).handleItemLeftClick(player, ITEM_STACK);
    }

    @Test
    public void handleShouldCallRightClickFunctionAndCancelEventBasedOnResult() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, ITEM_STACK, null, null);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemRightClick(player, ITEM_STACK)).thenReturn(false);

        when(actionHandlerProvider.get()).thenReturn(actionHandler);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameContextProvider, gameScope, actionHandlerProvider);
        eventHandler.handle(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(GAME_CONTEXT), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        assertThat(event.useItemInHand()).isEqualTo(Result.DENY);

        verify(actionHandler).handleItemRightClick(player, ITEM_STACK);
    }
}
