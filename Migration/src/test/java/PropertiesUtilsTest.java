import org.junit.jupiter.api.Test;
import utils.PropertiesUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesUtilsTest {
    @Test
    void testGetProperty() throws IOException {
        String fileName = "application.properties";
        Properties testProperties = new Properties();
        testProperties.setProperty("database.user", "snzhnn");

        try (OutputStream out = new FileOutputStream(fileName)) {
            testProperties.store(out, null);
        }

        PropertiesUtils propertiesUtils = new PropertiesUtils();
        assertEquals("snzhnn", propertiesUtils.getProperty("database.user"));

        assertTrue(new File(fileName).delete());
    }
}
