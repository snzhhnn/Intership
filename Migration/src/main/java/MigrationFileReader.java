import exception.InvalidClassNameException;
import exception.MigrationExecutedException;
import model.Schema;
import repository.MigrationRepository;
import repository.SchemaRepository;
import utils.ExtractUtils;
import utils.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MigrationFileReader {

    private final SchemaRepository repo;
    private final MigrationRepository migrationRepository;

    private static final Logger log = Logger.getLogger(MigrationFileReader.class.getName());

    public MigrationFileReader(SchemaRepository schemaRepository, MigrationRepository migrationRepository) throws SQLException {
        this.repo = schemaRepository;
        this.migrationRepository = migrationRepository;
        this.init();
    }

    public void init() throws SQLException {
        String script = "";
        String prefix = null;
        File[] listOfFiles = readNameFiles();
        for (File file: listOfFiles) {
            script = file.getName();
            if (script.startsWith("V")) {
                prefix = script.substring(0, 1);
            }

            if (prefix == null) {
                throw new InvalidClassNameException();
            }

            String checksum;
            try {
                checksum = defineChecksum(file);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            if (!compareSum(checksum, script)) {
                log.warning("migration was executed");
                break;
            }

            Schema schema = createSchema(script);
            try {
                log.info("start execute migration");
                migrationRepository.executeFileMigration(file);
                schema.setSuccess(true);
                repo.save(schema);
            } catch (IOException e) {
                schema.setSuccess(false);
                repo.save(schema);
                e.printStackTrace();
            }
        }
    }

    private Schema createSchema(String script) {
        return Schema.builder()
                .version(Integer.parseInt(ExtractUtils.extractVersion(script)))
                .description(ExtractUtils.extractDescription(script))
                .script(script)
                .type(ExtractUtils.extractType(script))
                .checksum(script)
                .installedBy("postgresql")
                .build();
    }

    private boolean compareSum(String checksum, String shortName) throws SQLException {
        Schema schema = repo.getMigrationByScript(shortName);
        if (schema.getChecksum() == null) {
            return true;
        } else if (!schema.getChecksum().equals(checksum)) {
            log.log(Level.SEVERE, "an exception was thrown", new MigrationExecutedException());
            throw new MigrationExecutedException();
        }
        return false;
    }

    private String defineChecksum(File file) throws IOException {
        Path path = file.toPath();
        try (InputStream is = Files.newInputStream(path)) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        }
    }

    private static File[] readNameFiles() {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        File folder = new File(propertiesUtils.getProperty("catalog.migration.name"));
        return folder.listFiles();
    }
}