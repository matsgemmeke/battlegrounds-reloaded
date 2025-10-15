package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.*;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiveWeaponCommandTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final String[] ARGS = { "test", "weapon" };

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Player player;
    @Mock
    private Provider<PlayerRegistry> playerRegistryProvider;
    @Mock
    private Translator translator;
    @Mock
    private WeaponCreator weaponCreator;

    private GiveWeaponCommand command;

    @BeforeEach
    void setUp() {
        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn(new TextTemplate("test"));

        command = new GiveWeaponCommand(gameContextProvider, gameScope, playerRegistryProvider, translator, weaponCreator);
    }

    @Test
    void executeThrowsUnknownGameKeyExceptionWhenOpenModeGameKeyIsNotRegistered() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> command.execute(player, ARGS))
                .isInstanceOf(UnknownGameKeyException.class)
                .hasMessage("No game context found game key OPEN-MODE");
    }

    @Test
    void executeSendsErrorMessageToPlayerGivenArgsDoNoFormExistingWeaponName() {
        GameContext gameContext = mock(GameContext.class);
        String message = "message";

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath())).thenReturn(new TextTemplate(message));
        when(weaponCreator.exists("test weapon")).thenReturn(false);

        command.execute(player, ARGS);

        verify(player).sendMessage(message);
    }

    @Test
    void executeThrowsIllegalStateExceptionWhenUnableToFindGamePlayerInstanceForGivenPlayer() {
        GameContext gameContext = mock(GameContext.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(player.getName()).thenReturn("TestPlayer");
        when(weaponCreator.exists("test weapon")).thenReturn(true);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(gameScope).runInScope(eq(gameContext), any());

        assertThatThrownBy(() -> command.execute(player, ARGS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unable to find GamePlayer instance for player TestPlayer despite being registered");
    }

    @Test
    void executeGivesAssignedWeaponToPlayer() {
        String message = "weapon given: %bg_weapon%";
        GameContext gameContext = mock(GameContext.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));

        Weapon weapon = mock(Weapon.class);
        when(weapon.getItemStack()).thenReturn(itemStack);
        when(weapon.getName()).thenReturn("test");

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(translator.translate(TranslationKey.WEAPON_GIVEN.getPath())).thenReturn(new TextTemplate(message));
        when(weaponCreator.exists("test weapon")).thenReturn(true);
        when(weaponCreator.createWeapon(gamePlayer, "test weapon")).thenReturn(weapon);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(gameScope).runInScope(eq(gameContext), any());

        command.execute(player, ARGS);

        verify(inventory).addItem(itemStack);
        verify(player).sendMessage("weapon given: test");
    }
}
