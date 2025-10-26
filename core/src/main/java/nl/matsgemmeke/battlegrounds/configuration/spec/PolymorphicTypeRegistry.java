package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.effect.DamageEffectSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PolymorphicTypeRegistry {

    private static final Map<String, Map<String, Class<?>>> rules = new HashMap<>();

    static {
        register("effect-type", "DAMAGE", DamageEffectSpec.class);
    }

    public static void register(String keyName, String valueName, Class<?> targetClass) {
        rules.computeIfAbsent(keyName.toLowerCase(), k -> new HashMap<>()).put(valueName.toUpperCase(), targetClass);
    }

    public static Optional<Class<?>> resolve(String keyName, String valueName) {
        if (!rules.containsKey(keyName)) {
            return Optional.empty();
        }

        Map<String, Class<?>> map = rules.get(keyName);

        return Optional.ofNullable(map.get(valueName.toUpperCase()));
    }
}
