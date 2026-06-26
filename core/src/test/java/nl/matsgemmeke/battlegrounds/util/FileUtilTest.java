package nl.matsgemmeke.battlegrounds.util;

import nl.matsgemmeke.battlegrounds.configuration.ResourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilTest {

    @TempDir
    private File tempDir;

    @Test
    void deleteFolder() throws IOException {
        File resourceFolder = new File("src/test/resources/file-util");
        File tempFolder = new File(tempDir, "file-util");

        ResourceLoader resourceLoader = new ResourceLoader();
        resourceLoader.copyResource(resourceFolder.toURI(), tempFolder.toPath());

        FileUtil.deleteFolder(tempFolder);

        assertThat(tempFolder).doesNotExist();
        assertThat(new File(tempFolder, "a.txt")).doesNotExist();
        assertThat(new File(tempFolder, "folder")).doesNotExist();
        assertThat(new File(tempFolder, "folder/b.txt")).doesNotExist();
    }
}
