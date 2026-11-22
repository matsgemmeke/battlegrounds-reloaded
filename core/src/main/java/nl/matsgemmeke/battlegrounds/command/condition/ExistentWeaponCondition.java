package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.item.registry.ItemSpecRegistry;

public class ExistentWeaponCondition implements ParameterCondition<String> {

    private final ItemSpecRegistry itemSpecRegistry;
    private final Translator translator;

    @Inject
    public ExistentWeaponCondition(ItemSpecRegistry itemSpecRegistry, Translator translator) {
        this.itemSpecRegistry = itemSpecRegistry;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, String weaponName) {
        if (itemSpecRegistry.exists(weaponName)) {
            return;
        }

        Replacement weaponReplacement = Replacement.parsed("weapon", weaponName);

        throw translator.translateLegacyText(TranslationKey.WEAPON_NOT_EXISTS.getPath(), weaponReplacement)
                .map(ConditionFailedException::new)
                .orElseGet(ConditionFailedException::new);
    }
}
