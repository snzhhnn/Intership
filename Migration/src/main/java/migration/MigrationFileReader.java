package migration;

import lombok.extern.slf4j.Slf4j;
import model.Schema;
import repository.SchemaRepository;
import utils.ExtractUtils;
import utils.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

@Slf4j
public class MigrationFileReader {

    private final SchemaRepository repo;
    private final MigrationExecutor migrationExecutor;

    public MigrationFileReader(SchemaRepository schemaRepository, MigrationExecutor migrationExecutor) throws SQLException {
        this.repo = schemaRepository;
        this.migrationExecutor = migrationExecutor;
        this.init();
    }

    public void init() throws SQLException {
        Schema[] schemas = readMigrationFile(readNameFiles());
        for (Schema schema: schemas) {

            if (compareSum(schema.getChecksum(), schema.getScript())) {
                break;
            }

            repo.save(schema);
            try {
                migrationExecutor.execute(schema.getSql());
                schema.setSuccess(true);
                repo.update(schema);
            } catch (IOException e) {
                schema.setSuccess(false);
                repo.update(schema);
                e.printStackTrace();
            }
        }
    }

    private boolean compareSum(String checksum, String shortName) {
        Schema schema = repo.getMigrationByScript(shortName);
        if (schema.getChecksum() == null) {
            return false;
        } else if (schema.getChecksum().equals(checksum)) {
            log.warn("migration was executed");
            return true;
        }
        return true;
    }


    private Schema[] readMigrationFile(File[] listOfFiles)  {
        String script = "";
        String prefix = null;
        Schema[] schemas = new Schema[listOfFiles.length];
        for (int i=0; i< listOfFiles.length; i++) {
            script = listOfFiles[i].getName();
            ExtractUtils.extractPrefix(script, prefix);
            schemas[i] = (createSchema(script, defineChecksum(listOfFiles[i]), ExtractUtils.extractSQL(listOfFiles[i])));
        }
        return schemas;
    }


    private Schema createSchema(String script, String checksum, String sql) {
        return Schema.builder()
                .version(Integer.parseInt(ExtractUtils.extractVersion(script)))
                .description(ExtractUtils.extractDescription(script))
                .script(script)
                .type(ExtractUtils.extractType(script))
                .checksum(checksum)
                .installedBy("postgresql")
                .sql(sql)
                .build();
    }

    private String defineChecksum(File file) {
        Path path = file.toPath();
        try (InputStream is = Files.newInputStream(path)) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File[] readNameFiles() {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        File folder = new File(propertiesUtils.getProperty("catalog.migration.name"));
        return folder.listFiles();
    }
}