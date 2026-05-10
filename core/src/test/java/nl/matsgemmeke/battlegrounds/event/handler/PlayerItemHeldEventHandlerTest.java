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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
class PlayerItemHeldEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final int PREVIOUS_SLOT = 0;
    private static final int CURRENT_SLOT = 1;
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

    private PlayerItemHeldEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        eventHandler = new PlayerItemHeldEventHandler(gameContextProvider, gameScope, itemInteractionDispatcherProvider, playerRegistryProvider);
    }

    @Test
    @DisplayName("handle does nothing when player is not in any game context")
    void handle_playerNotInGameContext() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();

        verifyNoInteractions(gameScope);
    }

    @Test
    @DisplayName("handle throws EventHandlingException when game key of player has no corresponding game context")
    void handle_playerGameKeyWithoutContext() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerItemHeldEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle does nothing when player is not registered in player registry")
    void handle_playerNotRegistered() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.empty());

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @ParameterizedTest
    @DisplayName("handle cancels event based on result of the interaction dispatch")
    @CsvSource({
            "true,false,false,true",
            "false,true,false,true",
            "false,false,true,true",
            "false,false,false,false"
    })
    void handleCancelsEventBasedOnResultOfActionInvoker(boolean eventCancelled, boolean changeFromCancelEvent, boolean changeToCancelEvent, boolean expectedCancelled) {
        ItemStack changeFrom = new ItemStack(Material.IRON_HOE);
        ItemStack changeTo = new ItemStack(Material.IRON_HOE);
        DispatchResult changeFromResult = new DispatchResult(true, changeFromCancelEvent);
        DispatchResult changeToResult = new DispatchResult(true, changeToCancelEvent);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(inventory.getItem(CURRENT_SLOT)).thenReturn(changeTo);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);
        event.setCancelled(eventCancelled);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(itemInteractionDispatcherProvider.get()).thenReturn(itemInteractionDispatcher);
        when(itemInteractionDispatcher.dispatchChangeFrom(gamePlayer, changeFrom)).thenReturn(changeFromResult);
        when(itemInteractionDispatcher.dispatchChangeTo(gamePlayer, changeTo)).thenReturn(changeToResult);
        when(player.getInventory()).thenReturn(inventory);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isEqualTo(expectedCancelled);
    }
}
