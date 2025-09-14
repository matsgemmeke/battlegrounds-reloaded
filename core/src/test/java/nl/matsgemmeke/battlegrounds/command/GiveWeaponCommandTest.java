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
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class GiveWeaponCommandTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final String WEAPON_ID = "TEST_WEAPON";

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Player player;
    private Provider<PlayerRegistry> playerRegistryProvider;
    private Translator translator;
    private WeaponCreator weaponCreator;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        player = mock(Player.class);
        playerRegistryProvider = mock();
        weaponCreator = mock(WeaponCreator.class);

        translator = mock(Translator.class);
        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn(new TextTemplate("test"));
    }

    @Test
    public void executeThrowsUnknownGameKeyExceptionWhenOpenModeGameKeyIsNotRegistered() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        GiveWeaponCommand command = new GiveWeaponCommand(gameContextProvider, gameScope, playerRegistryProvider, translator, weaponCreator);

        assertThatThrownBy(() -> command.execute(player, WEAPON_ID))
                .isInstanceOf(UnknownGameKeyException.class)
                .hasMessage("No game context found game key OPEN-MODE");
    }

    @Test
    public void executeThrowsIllegalStateExceptionWhenUnableToFindGamePlayerInstanceForGivenPlayer() {
        GameContext gameContext = mock(GameContext.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(player.getName()).thenReturn("TestPlayer");

        GiveWeaponCommand command = new GiveWeaponCommand(gameContextProvider, gameScope, playerRegistryProvider, translator, weaponCreator);
        command.execute(player, WEAPON_ID);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        assertThatThrownBy(() -> runnableCaptor.getValue().run())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unable to find GamePlayer instance for player TestPlayer despite being registered");
    }

    @Test
    public void executeGivesAssignedWeaponToPlayer() {
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
        when(weaponCreator.createWeapon(gamePlayer, GameKey.ofOpenMode(), WEAPON_ID)).thenReturn(weapon);

        GiveWeaponCommand command = new GiveWeaponCommand(gameContextProvider, gameScope, playerRegistryProvider, translator, weaponCreator);
        command.execute(player, WEAPON_ID);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        verify(inventory).addItem(itemStack);
        verify(player).sendMessage("weapon given: test");
    }
}
