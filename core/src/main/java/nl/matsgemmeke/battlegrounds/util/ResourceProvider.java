package nl.matsgemmeke.battlegrounds.util;

import java.io.InputStream;

@FunctionalInterface
public interface ResourceProvider {

    InputStream getResource(String fileName);
}
