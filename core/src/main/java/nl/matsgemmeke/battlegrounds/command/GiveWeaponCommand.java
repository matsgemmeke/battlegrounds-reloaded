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
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GiveWeaponCommand extends CommandSource {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<PlayerRegistry> playerRegistryProvider;
    @NotNull
    private final Translator translator;
    @NotNull
    private final WeaponCreator weaponCreator;

    @Inject
    public GiveWeaponCommand(
            @NotNull GameContextProvider gameContextProvider,
            @NotNull GameScope gameScope,
            @NotNull Provider<PlayerRegistry> playerRegistryProvider,
            @NotNull Translator translator,
            @NotNull WeaponCreator weaponCreator
    ) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()).getText(), "bg giveweapon <weapon>");
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerRegistryProvider = playerRegistryProvider;
        this.translator = translator;
        this.weaponCreator = weaponCreator;
    }

    public void execute(@NotNull Player player, @NotNull String weaponId) {
        GameContext gameContext = gameContextProvider.getGameContext(GAME_KEY)
                .orElseThrow(() -> new UnknownGameKeyException("No game context found game key %s".formatted(GAME_KEY)));

        gameScope.runInScope(gameContext, () -> {
            PlayerRegistry playerRegistry = playerRegistryProvider.get();
            GamePlayer gamePlayer = playerRegistry.findByEntity(player)
                    .orElseThrow(() -> new IllegalStateException("Unable to find GamePlayer instance for player %s despite being registered".formatted(player.getName())));

            Weapon weapon = weaponCreator.createWeapon(gamePlayer, weaponId);

            player.getInventory().addItem(weapon.getItemStack());

            Map<String, Object> values = Map.of("bg_weapon", weapon.getName());
            String message = translator.translate(TranslationKey.WEAPON_GIVEN.getPath()).replace(values);

            player.sendMessage(message);
        });
    }
}
