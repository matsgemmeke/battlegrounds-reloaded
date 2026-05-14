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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
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
class PlayerSwapHandItemsEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final ItemStack MAIN_HAND_ITEM = createItemStack(Material.IRON_HOE);
    private static final ItemStack OFF_HAND_ITEM = createItemStack(Material.IRON_HOE);
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

    private PlayerSwapHandItemsEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        eventHandler = new PlayerSwapHandItemsEventHandler(gameContextProvider, gameScope, itemInteractionDispatcherProvider, playerRegistryProvider);
    }

    @Test
    @DisplayName("handle does nothing when player is not in a game context")
    void handle_playerNotInGameContext() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();

        verifyNoInteractions(gameScope);
    }

    @Test
    @DisplayName("handle throws EventHandlingException when game key of player has no corresponding game context")
    void handle_invalidGameKey() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerSwapHandItemsEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle does nothing when player is not registered in the player registry")
    void handle_playerNotRegistered() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.empty());

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        verifyNoInteractions(itemInteractionDispatcherProvider);
    }

    @ParameterizedTest
    @CsvSource({ "true,false,true", "false,true,true", "false,false,false" })
    @DisplayName("handle cancels event when either dispatch results calls to cancel the event")
    void handle_successful(boolean swapFromCancelEvent, boolean swapToCancelEvent, boolean expectedCancelled) {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);
        DispatchResult swapFromResult = new DispatchResult(true, swapFromCancelEvent);
        DispatchResult swapToResult = new DispatchResult(true, swapToCancelEvent);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(itemInteractionDispatcherProvider.get()).thenReturn(itemInteractionDispatcher);
        when(itemInteractionDispatcher.dispatchSwapFrom(gamePlayer, OFF_HAND_ITEM)).thenReturn(swapFromResult);
        when(itemInteractionDispatcher.dispatchSwapTo(gamePlayer, MAIN_HAND_ITEM)).thenReturn(swapToResult);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isEqualTo(expectedCancelled);
    }

    private static ItemStack createItemStack(Material material) {
        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getType()).thenReturn(material);

        return itemStack;
    }
}
