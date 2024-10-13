package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import nl.matsgemmeke.battlegrounds.item.WeaponProvider;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

        Map<String, Object> values = Map.of("bg_weapon", value);
        String message = translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
