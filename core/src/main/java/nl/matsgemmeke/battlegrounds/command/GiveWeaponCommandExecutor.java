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
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import org.bukkit.entity.Player;

import java.util.Map;

public class GiveWeaponCommandExecutor {

    private static final GameKey FREEPLAY_GAME_KEY = GameKey.ofFreeplay();

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ItemCreator> itemCreatorProvider;
    private final Provider<PlayerRegistry> playerRegistryProvider;
    private final Translator translator;

    @Inject
    public GiveWeaponCommandExecutor(
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            Translator translator,
            Provider<ItemCreator> itemCreatorProvider,
            Provider<PlayerRegistry> playerRegistryProvider
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.translator = translator;
        this.itemCreatorProvider = itemCreatorProvider;
        this.playerRegistryProvider = playerRegistryProvider;
    }

    public void execute(Player player, String weaponName) {
        GameContext gameContext = gameContextProvider.getGameContext(FREEPLAY_GAME_KEY)
                .orElseThrow(() -> new UnknownGameKeyException("No game context found game key %s".formatted(FREEPLAY_GAME_KEY)));

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
