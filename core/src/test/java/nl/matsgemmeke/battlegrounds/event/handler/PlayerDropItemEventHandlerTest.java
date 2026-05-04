package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemInteractionDispatcher;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.*;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
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
class PlayerDropItemEventHandlerTest {

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

    private PlayerDropItemEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        eventHandler = new PlayerDropItemEventHandler(gameContextProvider, gameScope, itemInteractionDispatcherProvider, playerRegistryProvider);
    }

    @Test
    @DisplayName("handle does nothing when player is not in a game context")
    void handle_playerNotInGameContext() {
        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    @DisplayName("handle throws EventHandlingException when unable to find game context for player game key")
    void handle_invalidGameKey() {
        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerDropItemEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle does nothing when player is not registered in player registry")
    void handle_playerNotRegistered() {
        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.empty());

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("handle invokes ItemInteractionDispatcher and cancels event according to dispatch result")
    void handle_successful(boolean resultCancelEvent, boolean expectedCancelled) {
        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);
        DispatchResult result = new DispatchResult(true, resultCancelEvent);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(itemInteractionDispatcherProvider.get()).thenReturn(itemInteractionDispatcher);
        when(itemInteractionDispatcher.dispatchDropItem(gamePlayer, ITEM_STACK)).thenReturn(result);
        when(item.getItemStack()).thenReturn(ITEM_STACK);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isEqualTo(expectedCancelled);
    }
}
