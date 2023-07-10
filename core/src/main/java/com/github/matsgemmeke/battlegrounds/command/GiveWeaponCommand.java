package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveWeaponCommand extends CommandSource {

    @NotNull
    private FreemodeContext freemodeContext;
    @NotNull
    private Translator translator;
    @NotNull
    private WeaponProvider weaponProvider;

    public GiveWeaponCommand(@NotNull FreemodeContext freemodeContext, @NotNull WeaponProvider weaponProvider, @NotNull Translator translator) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()), "bg giveweapon <weapon>");
        this.freemodeContext = freemodeContext;
        this.weaponProvider = weaponProvider;
        this.translator = translator;
    }

    public void execute(@NotNull Player player, @NotNull String weaponId) {
        WeaponFactory<?> weaponFactory = weaponProvider.getWeaponFactory(weaponId.toUpperCase());
        Weapon weapon = weaponFactory.make(freemodeContext, weaponId);

        BattlePlayer battlePlayer = freemodeContext.getBattlePlayer(player);
        battlePlayer.addItem(weapon);

        weapon.setHolder(battlePlayer);

        player.getInventory().addItem(weapon.getItemStack());

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_weapon", weapon.getName());

        player.sendMessage(translator.translate(TranslationKey.FREEMODE_WEAPON_GIVEN.getPath(), placeholder));
    }
}
