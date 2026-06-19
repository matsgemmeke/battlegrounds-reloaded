package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemInteractionDispatcher;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerInteractEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofFreeplay();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.FREEPLAY_MODE);
    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private GameScope gameScope;
    @Mock
    private ItemInteractionDispatcher itemInteractionDispatcher;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;
    @Mock
    private Provider<ItemInteractionDispatcher> itemInteractionDispatcherProvider;
    @Mock
    private Provider<PlayerRegistry> playerRegistryProvider;

    private PlayerInteractEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new PlayerInteractEventHandler(gameContextProvider, gameScope, itemInteractionDispatcherProvider, playerRegistryProvider);
    }

    @Test
    @DisplayName("handle does nothing when clicked item is null")
    void handle_nullItem() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, null);

        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DEFAULT);

        verifyNoInteractions(gameScope);
    }

    @Test
    @DisplayName("handle does nothing when event action is not a left or right click")
    void handle_noLeftOrRightClick() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.PHYSICAL, ITEM_STACK, null, null);

        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DEFAULT);

        verifyNoInteractions(gameScope);
    }

    @Test
    @DisplayName("handle does nothing when player is not in a game context")
    void handle_playerNotInGameContext() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, ITEM_STACK, null, null);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DEFAULT);

        verifyNoInteractions(gameScope);
    }

    @Test
    @DisplayName("handle throws EventHandlingException when game key of player has no corresponding game context")
    void handle_invalidGameKey() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, ITEM_STACK, null, null);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerInteractEvent for game key FREEPLAY, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle does nothing when player is not registered in game context")
    void handle_playerNotRegistered() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, ITEM_STACK, null, null);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.empty());

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DEFAULT);

        verifyNoInteractions(itemInteractionDispatcher);
    }

    @Test
    @DisplayName("handle calls left click on action dispatcher and cancels event based on result")
    void handle_leftClickAction() {
        DispatchResult result = new DispatchResult(true, true);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, ITEM_STACK, null, null);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(itemInteractionDispatcher.dispatchLeftClick(gamePlayer, ITEM_STACK)).thenReturn(result);
        when(itemInteractionDispatcherProvider.get()).thenReturn(itemInteractionDispatcher);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DENY);
    }

    @Test
    @DisplayName("handle calls right click on action dispatcher and cancels event based on result")
    void handle_rightClickAction() {
        DispatchResult result = new DispatchResult(true, true);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, ITEM_STACK, null, null);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(itemInteractionDispatcher.dispatchRightClick(gamePlayer, ITEM_STACK)).thenReturn(result);
        when(itemInteractionDispatcherProvider.get()).thenReturn(itemInteractionDispatcher);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.useItemInHand()).isEqualTo(Result.DENY);
    }
}
