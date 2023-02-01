package com.github.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import org.jetbrains.annotations.NotNull;

public class ExistentWeaponIdCondition implements ParameterCondition<String, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    @NotNull
    private Translator translator;
    @NotNull
    private WeaponProvider weaponProvider;

    public ExistentWeaponIdCondition(@NotNull WeaponProvider weaponProvider, @NotNull Translator translator) {
        this.weaponProvider = weaponProvider;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, String value) throws InvalidCommandArgument {
        if (weaponProvider.exists(value)) {
            return;
        }

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_weapon", value);

        throw new ConditionFailedException(translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath(), placeholder));
    }
}
