package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.WeaponProvider;
import nl.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveWeaponCommand extends CommandSource {

    @NotNull
    private GameContext context;
    @NotNull
    private Translator translator;
    @NotNull
    private WeaponProvider weaponProvider;

    public GiveWeaponCommand(
            @NotNull GameContext context,
            @NotNull Translator translator,
            @NotNull WeaponProvider weaponProvider
    ) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()), "bg giveweapon <weapon>");
        this.context = context;
        this.translator = translator;
        this.weaponProvider = weaponProvider;
    }

    public void execute(@NotNull Player player, @NotNull String weaponId) {
        ItemConfiguration configuration = weaponProvider.getItemConfiguration(weaponId);

        // This shouldn't be null because of the condition on this command, but validate it again in case something
        // might change in the future
        if (configuration == null) {
            throw new IllegalArgumentException("Unable to find a factory instance for weapon with the id " + weaponId);
        }

        GamePlayer gamePlayer = context.getPlayerRegistry().findByEntity(player);

        WeaponFactory factory = weaponProvider.getFactory(configuration);
        Weapon weapon = factory.make(configuration, context, gamePlayer);

        player.getInventory().addItem(weapon.getItemStack());

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_weapon", weapon.getName());

        player.sendMessage(translator.translate(TranslationKey.WEAPON_GIVEN.getPath(), placeholder));
    }
}
