package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
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
    private final WeaponCreator weaponCreator;

    @Inject
    public GiveWeaponCommand(GameContextProvider gameContextProvider, GameScope gameScope, Provider<PlayerRegistry> playerRegistryProvider, Translator translator, WeaponCreator weaponCreator) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()).getText(), "bg giveweapon <weapon>");
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerRegistryProvider = playerRegistryProvider;
        this.translator = translator;
        this.weaponCreator = weaponCreator;
    }

    public void execute(Player player, String[] args) {
        GameContext gameContext = gameContextProvider.getGameContext(GAME_KEY)
                .orElseThrow(() -> new UnknownGameKeyException("No game context found game key %s".formatted(GAME_KEY)));

        String weaponName = String.join(" ", args);

        if (!weaponCreator.exists(weaponName)) {
            Map<String, Object> values = Map.of("bg_weapon", weaponName);
            String message = translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath()).replace(values);

            player.sendMessage(message);
            return;
        }

        gameScope.runInScope(gameContext, () -> {
            PlayerRegistry playerRegistry = playerRegistryProvider.get();
            GamePlayer gamePlayer = playerRegistry.findByEntity(player)
                    .orElseThrow(() -> new IllegalStateException("Unable to find GamePlayer instance for player %s despite being registered".formatted(player.getName())));
            Weapon weapon = weaponCreator.createWeapon(gamePlayer, weaponName);

            Map<String, Object> values = Map.of("bg_weapon", weapon.getName());
            String message = translator.translate(TranslationKey.WEAPON_GIVEN.getPath()).replace(values);

            player.getInventory().addItem(weapon.getItemStack());
            player.sendMessage(message);
        });
    }
}
