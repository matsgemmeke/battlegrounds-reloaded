package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.*;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GiveWeaponCommandTest {

    private Game game;
    private GameContext context;
    private Player player;
    private Translator translator;
    private WeaponProvider weaponProvider;

    @Before
    public void setUp() {
        this.game = mock(Game.class);
        this.context = mock(GameContext.class);
        this.player = mock(Player.class);
        this.translator = mock(Translator.class);
        this.weaponProvider = mock(WeaponProvider.class);

        when(game.getContext()).thenReturn(context);
        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenGivenIncompatibleWeaponId() {
        GiveWeaponCommand command = new GiveWeaponCommand(game, translator, weaponProvider);
        command.execute(player, "fail");
    }

    @Test
    public void shouldGiveAssignedWeaponToPlayer() {
        String weaponId = "TEST_WEAPON";
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        ItemConfiguration configuration = mock(ItemConfiguration.class);
        when(weaponProvider.getItemConfiguration(weaponId)).thenReturn(configuration);

        Weapon weapon = mock(Weapon.class);
        when(weapon.getItemStack()).thenReturn(itemStack);
        when(weapon.getName()).thenReturn("test");

        WeaponFactory factory = mock(WeaponFactory.class);
        when(factory.make(eq(configuration), eq(game), eq(context), any())).thenReturn(weapon);

        when(weaponProvider.getFactory(configuration)).thenReturn(factory);

        GiveWeaponCommand command = new GiveWeaponCommand(game, translator, weaponProvider);
        command.execute(player, weaponId);

        verify(inventory).addItem(itemStack);
    }
}
