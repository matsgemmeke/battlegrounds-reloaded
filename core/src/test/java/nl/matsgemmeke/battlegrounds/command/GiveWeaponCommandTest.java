package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class GiveWeaponCommandTest {

    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private Player player;
    private Translator translator;
    private WeaponCreator weaponCreator;

    @BeforeEach
    public void setUp() {
        this.contextProvider = mock(GameContextProvider.class);
        this.gameKey = GameKey.ofTrainingMode();
        this.player = mock(Player.class);
        this.weaponCreator = mock(WeaponCreator.class);

        this.translator = mock(Translator.class);
        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn(new TextTemplate("test"));
    }

    @Test
    public void shouldThrowErrorWhenGivenIncompatibleWeaponId() {
        GiveWeaponCommand command = new GiveWeaponCommand(contextProvider, gameKey, translator, weaponCreator);

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

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(gamePlayer);

        when(contextProvider.getComponent(gameKey, PlayerRegistry.class)).thenReturn(playerRegistry);

        Weapon weapon = mock(Weapon.class);
        when(weapon.getItemStack()).thenReturn(itemStack);
        when(weapon.getName()).thenReturn("test");

        WeaponFactory factory = mock(WeaponFactory.class);
        when(factory.create(eq(configuration), eq(gameKey), any())).thenReturn(weapon);

        when(translator.translate(TranslationKey.WEAPON_GIVEN.getPath())).thenReturn(new TextTemplate(message));
        when(weaponCreator.getFactory(configuration)).thenReturn(factory);

        GiveWeaponCommand command = new GiveWeaponCommand(contextProvider, gameKey, translator, weaponCreator);
        command.execute(player, weaponId);

        verify(inventory).addItem(itemStack);
        verify(player).sendMessage(message);
    }
}
