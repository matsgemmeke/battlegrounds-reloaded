package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import nl.matsgemmeke.battlegrounds.item.WeaponProvider;
import nl.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
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
