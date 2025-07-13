package nl.matsgemmeke.battlegrounds.configuration.spec;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.regex.Pattern;

public class SpecPropertyUtils extends PropertyUtils {

    @Override
    public Property getProperty(Class<? extends Object> type, String name) {
        if (name.indexOf('-') > -1) {
            name = this.toCamelCase(name);
        }

        return super.getProperty(type, name);
    }

    private String toCamelCase(String value) {
        return Pattern.compile("-([a-z])")
                .matcher(value)
                .replaceAll(mr -> mr.group(1).toUpperCase());
    }
}
