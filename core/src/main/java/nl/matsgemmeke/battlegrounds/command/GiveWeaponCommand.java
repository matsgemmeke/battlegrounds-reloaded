package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
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
    private final GameKey trainingModeKey;
    @NotNull
    private final Translator translator;
    @NotNull
    private final WeaponCreator weaponCreator;

    @Inject
    public GiveWeaponCommand(
            @NotNull GameContextProvider contextProvider,
            @Named("TrainingMode") @NotNull GameKey trainingModeKey,
            @NotNull Translator translator,
            @NotNull WeaponCreator weaponCreator
    ) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()).getText(), "bg giveweapon <weapon>");
        this.contextProvider = contextProvider;
        this.trainingModeKey = trainingModeKey;
        this.translator = translator;
        this.weaponCreator = weaponCreator;
    }

    public void execute(@NotNull Player player, @NotNull String weaponId) {
        ItemConfiguration configuration = weaponCreator.getItemConfiguration(weaponId);

        // This shouldn't be null because of the condition on this command, but validate it again in case something
        // might change in the future
        if (configuration == null) {
            throw new IllegalArgumentException("Unable to find a factory instance for weapon with the id " + weaponId);
        }

        PlayerRegistry playerRegistry = contextProvider.getComponent(trainingModeKey, PlayerRegistry.class);
        GamePlayer gamePlayer = playerRegistry.findByEntity(player);

        WeaponFactory factory = weaponCreator.getFactory(configuration);
        Weapon weapon = factory.make(configuration, trainingModeKey, gamePlayer);

        player.getInventory().addItem(weapon.getItemStack());

        Map<String, Object> values = Map.of("bg_weapon", weapon.getName());
        String message = translator.translate(TranslationKey.WEAPON_GIVEN.getPath()).replace(values);

        player.sendMessage(message);
    }
}
