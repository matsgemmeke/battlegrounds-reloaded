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
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    private GamePlayer gamePlayer;
    @Mock
    private GameScope gameScope;
    @Mock
    private Item item;
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

    private EntityPickupItemEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new EntityPickupItemEventHandler(gameContextProvider, gameScope, itemInteractionDispatcherProvider, playerRegistryProvider);
    }

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
        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    @DisplayName("handle throws EventHandlingException when unable to find game context for player game key")
    void handle_playerGameKeyHasNoGameContext() {
        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process EntityPickupItemEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle does nothing when event player is not registered")
    void handle_itemStackHasNoActionExecutor() {
        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(item.getItemStack()).thenReturn(ITEM_STACK);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.empty());

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    @DisplayName("handle invokes item interaction dispatcher, and does not remove item when result is not handled")
    void handle_successfulWithoutItemRemove() {
        DispatchResult result = new DispatchResult(false, true);
        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(item.getItemStack()).thenReturn(ITEM_STACK);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(itemInteractionDispatcherProvider.get()).thenReturn(itemInteractionDispatcher);
        when(itemInteractionDispatcher.dispatchPickupItem(gamePlayer, ITEM_STACK)).thenReturn(result);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();

        verify(item, never()).remove();
    }

    @ParameterizedTest
    @CsvSource({
            "true,false,true",
            "true,true,true",
            "false,true,true",
            "false,false,false"
    })
    @DisplayName("handle invokes item interaction dispatcher, removes item and cancels event based on event state and result")
    void handle_successfulWithItemRemove(boolean resultCancelEvent, boolean eventCancelled, boolean expectedCancelled) {
        DispatchResult result = new DispatchResult(true, resultCancelEvent);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);
        event.setCancelled(eventCancelled);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(item.getItemStack()).thenReturn(ITEM_STACK);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(itemInteractionDispatcherProvider.get()).thenReturn(itemInteractionDispatcher);
        when(itemInteractionDispatcher.dispatchPickupItem(gamePlayer, ITEM_STACK)).thenReturn(result);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isEqualTo(expectedCancelled);

        verify(item).remove();
        verify(player).playSound(player, Sound.ENTITY_ITEM_PICKUP, 1, 1);
    }
}
