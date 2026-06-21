package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.registry.ItemSpecRegistry;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;

import java.util.Map;

public class GiveWeaponCommand {

    public static final String NAME = "giveweapon";
    public static final String USAGE = "/bg giveweapon <weapon>";
    public static final String SUGGESTION = "/bg giveweapon ";
    public static final String[] PERMISSIONS = new String[] { "battlegrounds.giveweapon" };
    private static final GameKey FREEPLAY_GAME_KEY = GameKey.ofFreeplay();

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final ItemSpecRegistry itemSpecRegistry;
    private final Provider<ItemCreator> itemCreatorProvider;
    private final Provider<PlayerRegistry> playerRegistryProvider;
    private final Translator translator;

    @Inject
    public GiveWeaponCommand(
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            ItemSpecRegistry itemSpecRegistry,
            Translator translator,
            Provider<ItemCreator> itemCreatorProvider,
            Provider<PlayerRegistry> playerRegistryProvider
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.itemSpecRegistry = itemSpecRegistry;
        this.translator = translator;
        this.itemCreatorProvider = itemCreatorProvider;
        this.playerRegistryProvider = playerRegistryProvider;
    }

    public void execute(Player player, String[] args) {
        GameContext gameContext = gameContextProvider.getGameContext(FREEPLAY_GAME_KEY)
                .orElseThrow(() -> new UnknownGameKeyException("No game context found game key %s".formatted(FREEPLAY_GAME_KEY)));

        String weaponName = String.join(" ", args);

        if (!itemSpecRegistry.exists(weaponName)) {
            Map<String, Object> values = Map.of("bg_weapon", weaponName);
            String message = translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath()).replace(values);

            player.sendMessage(message);
            return;
        }

        gameScope.runInScope(gameContext, () -> this.giveWeapon(player, weaponName));
    }

    private void giveWeapon(Player player, String weaponName) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("Unable to find GamePlayer instance for player %s despite being registered".formatted(player.getName())));

        ItemCreator itemCreator = itemCreatorProvider.get();
        Weapon weapon = itemCreator.createWeapon(gamePlayer, weaponName);

        Map<String, Object> values = Map.of("bg_weapon", weapon.getName());
        String message = translator.translate(TranslationKey.WEAPON_GIVEN.getPath()).replace(values);

        player.getInventory().addItem(weapon.getItemStack());
        player.sendMessage(message);
    }
}
