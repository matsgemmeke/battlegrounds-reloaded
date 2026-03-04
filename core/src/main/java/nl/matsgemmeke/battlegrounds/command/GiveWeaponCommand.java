package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.weapon.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;

import java.util.Map;

public class GiveWeaponCommand extends CommandSource {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<PlayerRegistry> playerRegistryProvider;
    private final Translator translator;
    private final Provider<WeaponCreator> weaponCreatorProvider;

    @Inject
    public GiveWeaponCommand(
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            Translator translator,
            Provider<PlayerRegistry> playerRegistryProvider,
            Provider<WeaponCreator> weaponCreatorProvider
    ) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()).getText(), "bg giveweapon <weapon>");
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.translator = translator;
        this.playerRegistryProvider = playerRegistryProvider;
        this.weaponCreatorProvider = weaponCreatorProvider;
    }

    public void execute(Player player, String[] args) {
        GameContext gameContext = gameContextProvider.getGameContext(GAME_KEY)
                .orElseThrow(() -> new UnknownGameKeyException("No game context found game key %s".formatted(GAME_KEY)));

        String weaponName = String.join(" ", args);

        gameScope.runInScope(gameContext, () -> this.giveWeapon(player, weaponName));
    }

    private void giveWeapon(Player player, String weaponName) {
        WeaponCreator weaponCreator = weaponCreatorProvider.get();

        if (!weaponCreator.exists(weaponName)) {
            Map<String, Object> values = Map.of("bg_weapon", weaponName);
            String message = translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath()).replace(values);

            player.sendMessage(message);
            return;
        }

        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("Unable to find GamePlayer instance for player %s despite being registered".formatted(player.getName())));
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, weaponName);

        Map<String, Object> values = Map.of("bg_weapon", weapon.getName());
        String message = translator.translate(TranslationKey.WEAPON_GIVEN.getPath()).replace(values);

        player.getInventory().addItem(weapon.getItemStack());
        player.sendMessage(message);
    }
}
