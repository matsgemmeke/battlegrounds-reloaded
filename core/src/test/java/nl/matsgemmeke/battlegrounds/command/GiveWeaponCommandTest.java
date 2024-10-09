package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import nl.matsgemmeke.battlegrounds.item.*;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GiveWeaponCommandTest {

    private GameContext context;
    private Player player;
    private Translator translator;
    private WeaponProvider weaponProvider;

    @Before
    public void setUp() {
        this.context = mock(GameContext.class);
        this.player = mock(Player.class);
        this.translator = mock(Translator.class);
        this.weaponProvider = mock(WeaponProvider.class);

        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenGivenIncompatibleWeaponId() {
        GiveWeaponCommand command = new GiveWeaponCommand(context, translator, weaponProvider);
        command.execute(player, "fail");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGiveAssignedWeaponToPlayer() {
        String weaponId = "TEST_WEAPON";
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        ItemConfiguration configuration = mock(ItemConfiguration.class);
        when(weaponProvider.getItemConfiguration(weaponId)).thenReturn(configuration);

        GamePlayer gamePlayer = mock(GamePlayer.class);

        EntityRegistry<GamePlayer, Player> playerRegistry = (EntityRegistry<GamePlayer, Player>) mock(EntityRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(gamePlayer);
        when(context.getPlayerRegistry()).thenReturn(playerRegistry);

        Weapon weapon = mock(Weapon.class);
        when(weapon.getItemStack()).thenReturn(itemStack);
        when(weapon.getName()).thenReturn("test");

        WeaponFactory factory = mock(WeaponFactory.class);
        when(factory.make(eq(configuration), eq(context), any())).thenReturn(weapon);

        when(weaponProvider.getFactory(configuration)).thenReturn(factory);

        GiveWeaponCommand command = new GiveWeaponCommand(context, translator, weaponProvider);
        command.execute(player, weaponId);

        verify(inventory).addItem(itemStack);
    }
}
