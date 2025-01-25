package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class GiveWeaponCommandTest {

    private GameContext context;
    private Player player;
    private Translator translator;
    private WeaponCreator weaponCreator;

    @BeforeEach
    public void setUp() {
        this.context = mock(GameContext.class);
        this.player = mock(Player.class);
        this.translator = mock(Translator.class);
        this.weaponCreator = mock(WeaponCreator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn(new TextTemplate("test"));
    }

    @Test
    public void shouldThrowErrorWhenGivenIncompatibleWeaponId() {
        GiveWeaponCommand command = new GiveWeaponCommand(context, translator, weaponCreator);

        assertThrows(IllegalArgumentException.class, () -> command.execute(player, "fail"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGiveAssignedWeaponToPlayer() {
        String message = "weapon given";
        String weaponId = "TEST_WEAPON";
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        ItemConfiguration configuration = mock(ItemConfiguration.class);
        when(weaponCreator.getItemConfiguration(weaponId)).thenReturn(configuration);

        GamePlayer gamePlayer = mock(GamePlayer.class);

        EntityRegistry<GamePlayer, Player> playerRegistry = (EntityRegistry<GamePlayer, Player>) mock(EntityRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(gamePlayer);
        when(context.getPlayerRegistry()).thenReturn(playerRegistry);

        Weapon weapon = mock(Weapon.class);
        when(weapon.getItemStack()).thenReturn(itemStack);
        when(weapon.getName()).thenReturn("test");

        WeaponFactory factory = mock(WeaponFactory.class);
        when(factory.make(eq(configuration), eq(context), any())).thenReturn(weapon);

        when(translator.translate(TranslationKey.WEAPON_GIVEN.getPath())).thenReturn(new TextTemplate(message));
        when(weaponCreator.getFactory(configuration)).thenReturn(factory);

        GiveWeaponCommand command = new GiveWeaponCommand(context, translator, weaponCreator);
        command.execute(player, weaponId);

        verify(inventory).addItem(itemStack);
        verify(player).sendMessage(message);
    }
}
