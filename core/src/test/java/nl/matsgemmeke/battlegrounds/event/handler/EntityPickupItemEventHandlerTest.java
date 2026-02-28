package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.item.ActionExecutorRegistry;
import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
import nl.matsgemmeke.battlegrounds.item.action.PickupActionResult;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityPickupItemEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Item item;
    @Mock
    private Provider<ActionExecutorRegistry> actionExecutorRegistryProvider;
    @InjectMocks
    private EntityPickupItemEventHandler eventHandler;

    @Test
    @DisplayName("handle does nothing when entity is not a player")
    void handle_entityIsNoPlayer() {
        Zombie zombie = mock(Zombie.class);

        EntityPickupItemEvent event = new EntityPickupItemEvent(zombie, item, 0);

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    @DisplayName("handle does nothing when player is not in any game")
    void handle_playerNotInGame() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    @DisplayName("handle throws EventHandlingException when unable to find game context for player game key")
    void handle_playerGameKeyHasNoGameContext() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process EntityPickupItemEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle does nothing when no action executor is found for item stack")
    void handle_itemStackHasNoActionExecutor() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        ActionExecutorRegistry actionExecutorRegistry = mock(ActionExecutorRegistry.class);
        when(actionExecutorRegistry.getActionExecutor(ITEM_STACK)).thenReturn(Optional.empty());

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(actionExecutorRegistryProvider.get()).thenReturn(actionExecutorRegistry);
        when(item.getItemStack()).thenReturn(ITEM_STACK);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "true,false,false",
            "true,true,true",
            "false,false,true",
            "false,true,true"
    })
    @DisplayName("handle invokes action executor, removes item and cancels event based on event state and result")
    void handleInvokesActionExecutorAndCancelsEventAccordingToActionResult(boolean performAction, boolean eventCancelled, boolean expectedCancelled) {
        PickupActionResult result = new PickupActionResult(performAction, true);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handlePickupAction(player, ITEM_STACK)).thenReturn(result);

        ActionExecutorRegistry actionExecutorRegistry = mock(ActionExecutorRegistry.class);
        when(actionExecutorRegistry.getActionExecutor(ITEM_STACK)).thenReturn(Optional.of(actionExecutor));

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);
        event.setCancelled(eventCancelled);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(actionExecutorRegistryProvider.get()).thenReturn(actionExecutorRegistry);
        when(item.getItemStack()).thenReturn(ITEM_STACK);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isEqualTo(expectedCancelled);

        verify(item).remove();
    }
}
