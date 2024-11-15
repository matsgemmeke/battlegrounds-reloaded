package nl.matsgemmeke.battlegrounds.configuration;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class ResourceLoaderTest {

    @Test
    public void shouldLoad() throws IOException {
        URI source = new File("src/main/resources/items").toURI();
        URI targetUri = URI.create(this.getClass().getProtectionDomain().getCodeSource().getLocation() + "/items");
        Path target = Path.of(targetUri);

        ResourceLoader loader = new ResourceLoader();
        loader.copyResource(source, target);

        System.out.println(target);
    }
}
