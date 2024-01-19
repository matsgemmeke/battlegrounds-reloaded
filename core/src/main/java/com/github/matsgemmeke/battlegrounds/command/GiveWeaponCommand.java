package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
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
    private TrainingMode trainingMode;
    @NotNull
    private Translator translator;
    @NotNull
    private WeaponProvider weaponProvider;

    public GiveWeaponCommand(@NotNull TrainingMode trainingMode, @NotNull Translator translator, @NotNull WeaponProvider weaponProvider) {
        super("giveweapon", translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath()), "bg giveweapon <weapon>");
        this.trainingMode = trainingMode;
        this.translator = translator;
        this.weaponProvider = weaponProvider;
    }

    public void execute(@NotNull Player player, @NotNull String weaponId) {
        WeaponFactory<?> weaponFactory = weaponProvider.getWeaponFactory(weaponId.toUpperCase());
        Weapon weapon = weaponFactory.make(trainingMode, weaponId);

        BattlePlayer battlePlayer = trainingMode.getBattlePlayer(player);
        battlePlayer.addItem(weapon);

        weapon.setHolder(battlePlayer);

        player.getInventory().addItem(weapon.getItemStack());

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_weapon", weapon.getName());

        player.sendMessage(translator.translate(TranslationKey.TRAINING_MODE_WEAPON_GIVEN.getPath(), placeholder));
    }
}
