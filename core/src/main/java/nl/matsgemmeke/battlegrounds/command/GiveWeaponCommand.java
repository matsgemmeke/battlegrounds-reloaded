package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GiveWeaponCommand extends CommandSource {

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final GameKey openModeGameKey;
    @NotNull
    private final Translator translator;
    @NotNull
    private final WeaponCreator weaponCreator;

    @Inject
    public GiveWeaponCommand(
            @NotNull GameContextProvider contextProvider,
            @Named("OpenMode") @NotNull GameKey openModeGameKey,
            @NotNull Translator translator,
            @NotNull WeaponCreator weaponCreator
    ) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()).getText(), "bg giveweapon <weapon>");
        this.contextProvider = contextProvider;
        this.openModeGameKey = openModeGameKey;
        this.translator = translator;
        this.weaponCreator = weaponCreator;
    }

    public void execute(@NotNull Player player, @NotNull String weaponId) {
        PlayerRegistry playerRegistry = contextProvider.getComponent(openModeGameKey, PlayerRegistry.class);
        GamePlayer gamePlayer = playerRegistry.findByEntity(player);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, openModeGameKey, weaponId);

        player.getInventory().addItem(weapon.getItemStack());

        Map<String, Object> values = Map.of("bg_weapon", weapon.getName());
        String message = translator.translate(TranslationKey.WEAPON_GIVEN.getPath()).replace(values);

        player.sendMessage(message);
    }
}
