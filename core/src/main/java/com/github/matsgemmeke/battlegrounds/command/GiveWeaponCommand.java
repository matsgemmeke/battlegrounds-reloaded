package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveWeaponCommand extends CommandSource {

    @NotNull
    private Game game;
    @NotNull
    private GameContext context;
    @NotNull
    private Translator translator;
    @NotNull
    private WeaponProvider weaponProvider;

    public GiveWeaponCommand(
            @NotNull Game game,
            @NotNull GameContext context,
            @NotNull Translator translator,
            @NotNull WeaponProvider weaponProvider
    ) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()), "bg giveweapon <weapon>");
        this.game = game;
        this.context = context;
        this.translator = translator;
        this.weaponProvider = weaponProvider;
    }

    public void execute(@NotNull Player player, @NotNull String weaponId) {
        WeaponFactory<?> weaponFactory = weaponProvider.getWeaponFactory(weaponId.toUpperCase());
        Weapon weapon = weaponFactory.make(context, weaponId);

        GamePlayer gamePlayer = game.getGamePlayer(player);
        gamePlayer.addItem(weapon);

        weapon.setHolder(gamePlayer);

        player.getInventory().addItem(weapon.getItemStack());

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_weapon", weapon.getName());

        player.sendMessage(translator.translate(TranslationKey.WEAPON_GIVEN.getPath(), placeholder));
    }
}
