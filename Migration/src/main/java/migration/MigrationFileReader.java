package migration;

import lombok.extern.slf4j.Slf4j;
import model.Migration;
import org.flywaydb.core.Flyway;
import repository.MigrationRepository;
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

    private final MigrationRepository repo;
    private final MigrationExecutor migrationExecutor;

    public MigrationFileReader(MigrationRepository migrationRepository, MigrationExecutor migrationExecutor) throws SQLException {
        this.repo = migrationRepository;
        this.migrationExecutor = migrationExecutor;
        this.init();
    }

    public void init() throws SQLException {
        Migration[] migrations = readMigrationFile(readNameFiles());
        for (Migration migration : migrations) {

            if (compareSum(migration.getChecksum(), migration.getScript())) {
                continue;
            }

            repo.save(migration);
            try {
                migrationExecutor.execute(migration.getSql());
                migration.setSuccess(true);
                repo.update(migration);
            } catch (IOException e) {
                migration.setSuccess(false);
                repo.update(migration);
                e.printStackTrace();
            }
        }
    }

    private boolean compareSum(String checksum, String shortName) {
        Migration migration = repo.getMigrationByScript(shortName);
        if (migration.getChecksum() == null) {
            return false;
        } else if (migration.getChecksum().equals(checksum)) {
            log.warn("migration " + migration.getScript() + " was executed");
            return true;
        }
        return true;
    }


    private Migration[] readMigrationFile(File[] listOfFiles)  {
        String script = "";
        Migration[] migrations = new Migration[listOfFiles.length];
        for (int i=0; i< listOfFiles.length; i++) {
            script = listOfFiles[i].getName();
            ExtractUtils.extractPrefix(script);
            migrations[i] = (createSchema(script, defineChecksum(listOfFiles[i]), ExtractUtils.extractSQL(listOfFiles[i])));
        }
        return migrations;
    }


    private Migration createSchema(String script, String checksum, String sql) {
        return Migration.builder()
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
        File folder = new File(propertiesUtils.getProperty("db/migration"));
        return folder.listFiles();
    }
}