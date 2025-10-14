package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

import java.util.Map;

public class ExistentWeaponNameCondition implements ParameterCondition<String, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    private final Translator translator;
    private final WeaponCreator weaponCreator;

    @Inject
    public ExistentWeaponNameCondition(WeaponCreator weaponCreator, Translator translator) {
        this.weaponCreator = weaponCreator;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, String value) throws InvalidCommandArgument {
        if (weaponCreator.exists(value)) {
            return;
        }

        Map<String, Object> values = Map.of("bg_weapon", value);
        String message = translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
