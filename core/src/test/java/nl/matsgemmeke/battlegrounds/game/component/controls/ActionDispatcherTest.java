package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemActionHandler;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionDispatcherTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private ItemActionHandler<Gun> gunActionHandler;
    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private ActionDispatcher actionDispatcher;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        actionDispatcher.registerActionHandler(gunActionHandler);
    }

    @Test
    @DisplayName("dispatch returns UNHANDLED when given player is not registered")
    void dispatch_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.empty());

        DispatchResult result = actionDispatcher.dispatch(player, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result).isEqualTo(DispatchResult.UNHANDLED);
    }

    @ParameterizedTest
    @CsvSource({ "HANDLED,HANDLED", "CANCELLED,CANCELLED" })
    @DisplayName("dispatch returns dispatch result from underlying action handler when one resolves the given player and item stack")
    void dispatch_actionHandlerResolvesGivenPlayerAndItemStack(DispatchResult actionHandlerResult, DispatchResult expectedResult) {
        Gun gun = mock(Gun.class);

        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunActionHandler.resolve(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gunActionHandler.dispatch(gun, Action.LEFT_CLICK)).thenReturn(actionHandlerResult);

        DispatchResult result = actionDispatcher.dispatch(player, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("dispatch returns UNHANDLED when none of the action handlers resolves the given player and item stack")
    void dispatch_noActionHandlerResolvesGivenPlayerAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunActionHandler.resolve(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        DispatchResult result = actionDispatcher.dispatch(player, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result).isEqualTo(DispatchResult.UNHANDLED);
    }
}
