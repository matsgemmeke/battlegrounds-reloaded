package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.item.*;
import nl.matsgemmeke.battlegrounds.item.registry.ItemSpecRegistry;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiveWeaponCommandTest {

    private static final GameKey GAME_KEY = GameKey.ofFreeplay();
    private static final String[] ARGS = { "test", "weapon" };
    private static final UUID UNIQUE_ID = UUID.randomUUID();

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private ItemSpecRegistry itemSpecRegistry;
    @Mock
    private Player player;
    @Mock
    private Provider<PlayerRegistry> playerRegistryProvider;
    @Mock
    private Provider<ItemCreator> itemCreatorProvider;
    @Mock
    private Translator translator;

    private GiveWeaponCommand command;

    @BeforeEach
    void setUp() {
        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn(new TextTemplate("test"));

        command = new GiveWeaponCommand(gameContextProvider, gameScope, itemSpecRegistry, translator, itemCreatorProvider, playerRegistryProvider);
    }

    @Test
    @DisplayName("execute throws UnknownGameKeyException when open mode game key is not registered")
    void execute_freeplayGameKeyNotRegistered() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> command.execute(player, ARGS))
                .isInstanceOf(UnknownGameKeyException.class)
                .hasMessage("No game context found game key FREEPLAY");
    }

    @Test
    @DisplayName("execute sends error message to player when given args do not form a valid weapon name")
    void execute_invalidWeaponName() {
        GameContext gameContext = mock(GameContext.class);
        String message = "message";

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(itemSpecRegistry.exists("test weapon")).thenReturn(false);
        when(translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath())).thenReturn(new TextTemplate(message));

        command.execute(player, ARGS);

        verify(player).sendMessage(message);
    }

    @Test
    @DisplayName("execute throws IllegalStateException when unable to find GamePlayer instance for given player")
    void execute_unknownPlayer() {
        GameContext gameContext = mock(GameContext.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(itemSpecRegistry.exists("test weapon")).thenReturn(true);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(player.getName()).thenReturn("TestPlayer");
        when(player.getUniqueId()).thenReturn(UNIQUE_ID);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));

        assertThatThrownBy(() -> command.execute(player, ARGS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unable to find GamePlayer instance for player TestPlayer despite being registered");
    }

    @Test
    @DisplayName("execute gives assigned weapon to player")
    void execute_givesWeaponToPlayer() {
        String message = "weapon given: %bg_weapon%";
        GameContext gameContext = mock(GameContext.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));

        Weapon weapon = mock(Weapon.class);
        when(weapon.getItemStack()).thenReturn(itemStack);
        when(weapon.getName()).thenReturn("test");

        ItemCreator itemCreator = mock(ItemCreator.class);
        when(itemCreator.createWeapon(gamePlayer, "test weapon")).thenReturn(weapon);

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(itemCreatorProvider.get()).thenReturn(itemCreator);
        when(itemSpecRegistry.exists("test weapon")).thenReturn(true);
        when(player.getUniqueId()).thenReturn(UNIQUE_ID);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(translator.translate(TranslationKey.WEAPON_GIVEN.getPath())).thenReturn(new TextTemplate(message));

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));

        command.execute(player, ARGS);

        verify(inventory).addItem(itemStack);
        verify(player).sendMessage("weapon given: test");
    }
}
