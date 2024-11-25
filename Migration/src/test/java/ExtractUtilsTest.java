import exception.InvalidClassNameException;
import org.junit.jupiter.api.Test;
import utils.ExtractUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class ExtractUtilsTest {

    @Test
    void testExtractVersion() {
        String shortName = "V1_create_user.sql";
        assertEquals("1", ExtractUtils.extractVersion(shortName));
    }

    @Test
    void testExtractDescription() {
        String shortName = "V10_hello_world.sql";
        assertEquals("hello world", ExtractUtils.extractDescription(shortName));
    }

    @Test
    void testExtractType() {
        String shortName = "V4_create_student.sql";
        assertEquals(".sql", ExtractUtils.extractType(shortName));
    }

    @Test
    void testExtractPrefixValid() {
        String script = "V1_goodbye_world.sql";
        assertEquals("V", ExtractUtils.extractPrefix(script));
    }

    @Test
    void testExtractPrefixInvalid() {
        String script = "InvalidName.sql";
        assertThrows(InvalidClassNameException.class, () -> ExtractUtils.extractPrefix(script));
    }

    @Test
    void testExtractSQL() throws IOException {
        File tempFile = File.createTempFile("test", ".sql");
        String sqlContent = "SELECT * FROM users;";
        Files.writeString(tempFile.toPath(), sqlContent);

        assertEquals(sqlContent, ExtractUtils.extractSQL(tempFile));

        assertTrue(tempFile.delete());
    }
}