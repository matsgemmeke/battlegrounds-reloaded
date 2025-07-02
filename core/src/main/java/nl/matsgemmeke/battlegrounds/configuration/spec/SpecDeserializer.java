package nl.matsgemmeke.battlegrounds.configuration.spec;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SpecDeserializer {

    @NotNull
    public <T> T deserializeSpec(@NotNull File file, @NotNull Class<T> type) {
        InputStream inputStream;
        String path = file.getPath().replace("\\", "/");

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new SpecDeserializationException("The given spec file at %s cannot be found".formatted(path));
        }

        SpecPropertyUtils propertyUtils = new SpecPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);

        Constructor constructor = new Constructor(type, new LoaderOptions());
        constructor.setPropertyUtils(propertyUtils);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);

        Representer representer = new Representer(dumperOptions);

        Yaml yaml = new Yaml(constructor, representer);
        return yaml.loadAs(inputStream, type);
    }
}
